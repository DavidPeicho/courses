import networkx as nx
import math
import subprocess

class Benchmark:
    presets_dic = {
        'vfast' : {
            'nmax' : 100, 'kmax' : 75, 'nstep' : 10, 'kstep' : 5,
            'nstart' : 5, 'kstart' : 5
        },
        'fast' : {
            'nmax' : 1000, 'kmax' : 50, 'nstep' : 100, 'kstep' : 5,
            'nstart' : 100, 'kstart' : 5
        },
        'medium' : {
            'nmax' : 1000, 'kmax' : 250, 'nstep' : 100, 'kstep' : 25,
            'nstart' : 100, 'kstart' : 25
        },
        'slow' : {
            'nmax' : 1000, 'kmax' : 500, 'nstep' : 200, 'kstep' : 150,
            'nstart' : 200, 'kstart' : 50
        },
        'vslow' : {
            'nmax' : 10000, 'kmax' : 1000, 'nstep' : 500, 'kstep' : 150,
            'nstart' : 1000, 'kstart' : 150
        }
    }
    # TODO: Handles Unix systems
    converter_path = 'src/findcommunities/build/convert.exe'
    community_path = 'src/findcommunities/build/community.exe'

    @staticmethod
    def compute(nmax, kmax, nstep, kstep, nstart=1, kstart=2):
        bench_results = {
            'n_data' : [],
            'k_data' : [],
            'time' : [],
        }

        nb_n = math.ceil((nmax - nstart) / nstep)
        nb_k = math.ceil((kmax - kstart) / kstep)
        nb_loops_to_compute = int(nb_n * nb_k)
        nb_loop = 0
        # Generates several combinations of Caveman graph by
        # making variation on the n and k parameters.
        clique_nb = nstart
        size = kstart

        while clique_nb < nmax:

            while size < kmax:

                # Prints some information about the process,
                # such as the progress percentage, the n, or the k parameters
                progress = float(nb_loop) / float(nb_loops_to_compute)
                progress = 1.0 if progress >= 1.0 else progress
                progress_str = 'Progress: {0:.2f}%'.format(progress * 100.0)
                progress_str = progress_str + ' | ' + 'Current params: (n={}, k={})'.format(clique_nb, size)
                print(progress_str)

                # Builds the graph using the networkx library
                graph = nx.connected_caveman_graph(clique_nb, size)
                # Saves graph edges to disk
                nx.write_edgelist(graph, "raw_graph.tmp", data=False)
                # Converts txt graph to a binary form
                subprocess.call([Benchmark.converter_path, "-i",
                                "raw_graph.tmp", "-o", "graph.bin"])
                # Computes communities and retrieve time(ms) from stdout
                communities_proc = subprocess.Popen(
                    [Benchmark.community_path, 'graph.bin'],
                    stdout=subprocess.PIPE
                )
                out, _ = communities_proc.communicate()
                communities_proc.wait()

                time = int(out)
                print('time = {} ms'.format(time))

                # Caches values to draw them during the next step
                bench_results['n_data'].append(clique_nb)
                bench_results['k_data'].append(size)
                bench_results['time'].append(time)

                size = size + kstep
                nb_loop = nb_loop + 1

            size = kstart
            clique_nb = clique_nb + nstep

        print(nb_loop)

        print('Progress: 100%. Benchmark successful.')
        return bench_results

    @staticmethod
    def preset(presetStr):
        params = Benchmark.presets_dic[presetStr]
        return Benchmark.compute(
            params['nmax'], params['kmax'], params['nstep'],
            params['kstep'], params['nstart'], params['kstart']
        )
