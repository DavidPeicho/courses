#!/bin/sh

check_arg_with_value()
{
    if [ -z "$2" ]; then
        echo "Error: argument '$1' requires a value"
        exit 1
    fi
}

##
# Parses command line arguments
##
preset=""
while [ $# -ne 0 ] ; do
    key="$1"
    case $key in
        -p|--preset)
            preset="$2"
            check_arg_with_value $key $preset
            shift
        ;;
        *)
        ;;
    esac
    shift
done

##
# Checks O.S
##
python_bin=""
if [ "$OSTYPE" = "linux-gnu" ]; then
    python_bin="python3"
elif [ "$OSTYPE" = "darwin"* ]; then
    python_bin="python3"
elif [ "$OSTYPE" = "cygwin" ]; then
    python_bin="python.exe"    
elif [ "$OSTYPE" = "msys" ]; then
    python_bin="python.exe"
elif [ "$OSTYPE" = "win32" ]; then
    python_bin="python.exe"
else
    python_bin="python3"
fi

##
# Compile the findcommunities binaries
##
cd src/findcommunities
make all

##
# Runs benchmark with given preset
##
cd -
if [ -z "$python_bin" ]; then
    echo "first"
    "$python_bin" main.py -p "$preset"
else
    echo "second"
    "$python_bin" main.py
fi
