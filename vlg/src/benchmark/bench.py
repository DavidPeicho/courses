import subprocess
import networkx as nx

class Benchmark:
    presets_dic = {
        'vfast' : { 'nmax' : 1,'kmax' : 1, 'nstep' : 1, 'kstep' : 1 },
        'fast' : { 'nmax' : 1, 'kmax' : 1, 'nstep' : 1, 'kstep' : 1 },
        'medium' : { 'nmax' : 1, 'kmax' : 1, 'nstep' : 1, 'kstep' : 1 },
        'fast' : { 'nmax' : 1, 'kmax' : 1, 'nstep' : 1, 'kstep' : 1 },
        'vfast' : { 'nmax' : 1, 'kmax' : 1, 'nstep' : 1, 'kstep' : 1 }
    }
    # TODO: Handles Unix systems
    converter_path = 'src/findcommunities/build/convert.exe'

    @staticmethod
    def compute(nmax, kmax, nstep, kstep):
        # Generates several combinations of Caveman graph by
        # making variation on the n and k parameters.
        for clique_nb in range(1, nmax + 1):
            for size in range(2, kmax + 1):
                # Builds the graph using the networkx library
                graph = nx.connected_caveman_graph(clique_nb, size)
                # Saves graph edges to disk
                nx.write_edgelist(graph, "raw_graph.tmp", data=False)
                # Converts txt graph to a binary form
                subprocess.call([Benchmark.converter_path, "-i",
                                "raw_graph.tmp", "-o", "graph.bin"])

    @staticmethod
    def preset(presetStr):
        params = Benchmark.presets_dic[presetStr]
        Benchmark.compute(params.nmax, params.kmax, params.nstep, params.kstep)
