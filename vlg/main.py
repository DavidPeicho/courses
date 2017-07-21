import argparse
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

from src.benchmark.bench import Benchmark

def parse_args():
    desc =          """
                    Benchmarks communities on Caveman graphs generated by
                    the networkx python library. The code used to find the number of
                    communities is accessible at
                    https://sites.google.com/site/findcommunities/
                    """
    presetArgHelp = """
                    Specifies the preset to use. Presets are composed of several
                    cople (n, k), with n the number of cliques and k the size of
                    the clique. A vslow preset will be very long to compute compared
                    to a vfast one. Existing presets are any of the following:\n
                    vfast - fast - medium - slow - vslow
                    """
    nmaxArgHelp =   """
                    Specifies the max number of clique used by the benchmark.
                    """
    kmaxArgHelp =   """
                    Specifies the max size of each clique used by the benchmark.
                    """
    nstepArgHelp =  """
                    Specifies the step when benchmarking the n argument.
                    """
    kstepArgHelp =  """
                    Specifies the step when benchmarking the k argument.
                    """
    nstartArgHelp = """
                    Specifies the starting value of n.
                    """
    kstartArgHelp = """
                    Specifies the starting value of k.
                    """
    converterArgHelp =  """
                        Specifies the path to the converter binary. If it is not
                        specified, the script will go to:
                        src/findcommunities/build/convert.exe
                        """
    communityArgHelp =  """
                        Specifies the path to the community binary. If it is not
                        specified, the script will go to:
                        src/findcommunities/build/community.exe
                        """

    parser = argparse.ArgumentParser(description=desc)
    parser.add_argument('-p', '--preset', help=presetArgHelp, required=False)
    parser.add_argument('-nmax', '--nmax', help=nmaxArgHelp, required=False)
    parser.add_argument('-kmax', '--kmax', help=kmaxArgHelp, required=False)
    parser.add_argument('-nstep', '--nstep', help=nstepArgHelp, required=False)
    parser.add_argument('-kstep', '--kstep', help=kstepArgHelp, required=False)
    parser.add_argument('-nstart', '--nstart', help=nstartArgHelp, required=False)
    parser.add_argument('-kstart', '--kstart', help=kstartArgHelp, required=False)
    parser.add_argument('-gconv', '--graph_converter', help=converterArgHelp, required=False)
    parser.add_argument('-gcom', '--graph_community', help=communityArgHelp, required=False)

    return parser.parse_args()

if __name__ == "__main__":

    args = parse_args()
    
    # Sets up the path to the binary converting
    # text graph to binary.
    if not(args.graph_converter is None):
        Benchmark.converter_path = args.graph_converter
    # Sets up the path to the binary computing
    # the graph communities.
    if not(args.graph_community is None):
        Benchmark.community_path = args.graph_community

    bench_results = None

    # Checks whether the user asked to make the benchmark
    # using one of the available preset or not.
    if not(args.preset is None):
        if not(args.preset in Benchmark.presets_dic):
            print('Benchmark: the preset {} does not exist!'.format(args.preset))
            exit(1)
        # Launches benchmark with selected preset
        bench_results = Benchmark.preset(args.preset)
    else:
        # No preset given, launch the benchmark with user values
        # or default values.
        args.nmax = 10000 if args.nmax is None else int(args.nmax)
        args.kmax = 10000 if args.kmax is None else int(args.kmax)
        args.nstep = 500 if args.nstep is None else int(args.nstep)
        args.kstep = 1000 if args.kstep is None else int(args.kstep)
        args.nstart = 1000 if args.nstart is None else int(args.nstart)
        args.kstart = 1000 if args.kstart is None else int(args.kstart)
        bench_results = Benchmark.compute(
            args.nmax, args.kmax, args.nstep,
            args.kstep, args.nstart, args.kstart
        )

    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    ax.set_title('Benchmark of the time to compute the communities')  
    ax.set_xlabel('Number of cliques')
    ax.set_ylabel('Number of nodes per clique')
    ax.set_zlabel('Time (ms)')
    ax.plot_trisurf(
        bench_results['n_data'], bench_results['k_data'],
        bench_results['time'], cmap=plt.cm.terrain
    )
    plt.show()
