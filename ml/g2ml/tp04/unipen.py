import re
import os
import numpy as np
import matplotlib.pyplot as plt

def loadUnipenData(filename):
    segment = re.compile(r"^\.SEGMENT.*\?.*\"(\d)\"")
    point = re.compile(r" (\d+)  (\d+)")
    pendown = re.compile(r"\.PEN_DOWN")
    penup = re.compile(r"\.PEN_UP")
    f = open(filename)

    trace = np.empty((0 , 2), dtype=float)
    DATA = []
    Label = []
    keyword, args = None, None
    for line in f.readlines():
        m = segment.match(line)
        if m is not None:
            if keyword is None:
                keyword = "SEGMENT"
            else:
                DATA.append(trace)
                Label.append(args)
                trace = np.empty((0, 2), dtype=float)
            args = m.group(1)
        elif pendown.match(line) is not None:
            trace = np.concatenate((trace, np.array([[-1, -1]])), axis = 0)
        elif penup.match(line) is not None:
            trace = np.concatenate((trace, np.array([[-1, 1]])))
        else:
            p = point.match(line)
            if p is not None:
                trace = np.concatenate((trace, np.array([[float(p.group(1)), float(p.group(2))]])))

    DATA.append(trace)
    Label.append(args)

    f.close()
    return DATA, Label

def plotUnipen(data):
    ind = np.where(data[:, 0] == -1)[0]
    indDown = ind[np.where(data[ind, 1].flatten() == -1)]
    indUp   = ind[np.where(data[ind, 1].flatten() == 1)]
    for i in range(indDown.shape[0]):
        plt.plot(data[(indDown[i] + 1):(indUp[i] - 1), 0], data[(indDown[i] + 1):(indUp[i] - 1), 1], 'k')
    plt.show()
