class Op():
    NOT = 1
    AND = 2
    OR = 3
    DATA = 4

class Tree(object):
    """
    Binary Tree node structure.

    Used to represents a simple AST.
    
    @param
    op: the operance
    data: data if any (token for leaves node)
    left: left child
    right: right child
    """

    def __init__(self):
        self.left = None
        self.right = None
        self.op = None
        self.data = None

    def debug(self):
        str = "-----STARTING NEW NODE------\n"
        str += "OP: "
        if self.op == Op.AND:
            str += "AND\n"
        elif self.op == Op.OR:
            str += "OR\n"
        elif self.op == Op.NOT:
            str += "NOT\n"
        elif self.op == Op.DATA:
            str += "DATA\n"
            if self.data is None:
                str += "val = NONE\n"
            else:
                str += "val = " + self.data + "\n"
        print(str)

        if not(self.left is None):
            print(self.left.p())
        if not(self.right is None):
            print(self.right.p())
        if not(self.op == Op.DATA):
            print("-----END NODE------\n")

class Searcher:
    def search(self, str, index):
        """
        Searches for a single word in the given index
        """
        if not(str in index.wordToDids):
            return None

        dIds = index.wordToDids[str]
        urls = []
        for d in dIds:
            urls.append(index.didToUrl[d])

        return urls

    def search_ast(self, ast, index):
        """
        Step1: Builds an AST from the given query.

        Step2: Makes an evaluation of the AST, by performing set intersection,
        union, and negation.
        """
        
        tree = self._parse(ast, 0)
        dIds = self._eval(tree, index)
        
        urls = []
        for d in dIds:
            urls.append(index.didToUrl[d])

        return urls

    def _eval(self, tree, index):
        """
        Evaluates recursively the given AST.
        """
        if tree.op == Op.AND:
            return list(set(self._eval(tree.left, index)) & set(self._eval(tree.right, index)))
        elif tree.op == Op.OR:
            return list(set(self._eval(tree.left, index)) | set(self._eval(tree.right, index)))
        elif tree.op == Op.NOT:
            negation = []
            l = self._eval(tree.left, index)
            for key in index.wordToDids:
                negation = list(set(negation) | (set(set(index.wordToDids[key]) - set(l))))

            return negation
        elif tree.op == Op.DATA and not(tree.data is None):
            if not(tree.data in index.wordToDids):
                return []
            
            return index.wordToDids[tree.data]

        return []

    def _parse(self, ast, start):
        """
        Parses the string query into an AST structure
        """

        ast_len = len(ast)
        i = start
        while i  < ast_len:
            if ast[i] != '!':
                i = i + 1
                continue
            if i < ast_len - 3:
                if ast[i + 1] == 'O' and ast[i + 2] == 'R':
                    left_content = ast[start:i]
                    return self._parse_bin_op(ast, i + 3, left_content, Op.OR)
            if i < ast_len - 4:
                if ast[i + 1] == 'A' and ast[i + 2] == 'N' and ast[i + 3] == 'D':
                    left_content = ast[start:i]
                    return self._parse_bin_op(ast, i + 4, left_content, Op.AND)
                if ast[i + 1] == 'N' and ast[i + 2] == 'O' and ast[i + 3] == 'T':
                    return self._parse_not(ast, i + 4)
            i = i + 1

        node = Tree()
        node.op = Op.DATA
        node.data = ast[start:ast_len].strip(' \t\n\r')
        return node

    def _parse_bin_op(self, ast, start_idx, left_content, operator):

        left_node = Tree()
        left_node.op = Op.DATA
        left_node.data = left_content.strip(' \t\n\r')

        parent_node = Tree()
        parent_node.left = left_node
        parent_node.right = self._parse(ast, start_idx)
        parent_node.op = operator

        return parent_node

    def _parse_not(self, ast, start_idx):
        parent_node = Tree()
        parent_node.left = self._parse(ast, start_idx)
        parent_node.op = Op.NOT

        return parent_node

