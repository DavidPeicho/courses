import scipy.misc
import matplotlib.pyplot as plt

def plotImage(im):
    fig = plt.imshow(scipy.misc.toimage(im.reshape(28, 28),
                                        cmin=0.0, cmax=1.0),
                                        cmap='gray')
    fig.axes.get_xaxis().set_visible(False)
    fig.axes.get_yaxis().set_visible(False)


def plotGroupImages(ims):
    plt.figure()
    k = ims.shape[1]

    rows = k // 5 + 1
    columns = min(k, 5)

    for i in range(k):
        plt.subplot(rows, columns, i + 1)
        plotImage(ims[:, i])

