//************************************************
//*                                              *
//*   TP 1 TIRF (c) 2017 J. FABRIZIO             *
//*                                              *
//*                               LRDE EPITA     *
//*                                              *
//************************************************

#include "histogram.hh"

namespace tirf {

  histogram_1d
  computeHistogram(const gray8_image* img) {

    histogram_1d histogram;
    for (auto i = 0; i <= IMAGE_MAX_LEVEL; ++i) {
      histogram.histogram[i] = 0;
    } 

    auto& buffer = img->get_buffer();
    for (auto i = 0; i < img->sx * img->sy; ++i) {
      histogram.histogram[buffer[i]] += 1;
    }

    return histogram;
  }

  histogram_1d
  cumulativeHistogram(const histogram_1d& histogram) {

    histogram_1d result;
    for (auto i = 0; i <= IMAGE_MAX_LEVEL; ++i) {
      result.histogram[i] = 0;
    }

    result.histogram[0] = histogram.histogram[0];
    for (auto i = 1; i <= IMAGE_MAX_LEVEL; ++i) {
      result.histogram[i] = result.histogram[i - 1] + histogram.histogram[i];
    }

    return result;

  }

  void
  balance(gray8_image* img) {

    histogram_1d imgHistogram = computeHistogram(img);
    histogram_1d cumulativeHisto = cumulativeHistogram(imgHistogram);

    uint8_t maxValue = 0;
    for (auto i = 0; i <= IMAGE_MAX_LEVEL; ++i) {
      maxValue = (imgHistogram.histogram[i] > maxValue) ?
                  imgHistogram.histogram[i] : maxValue;
    }

    int sx = img->sx;
    int sy = img->sy;

    for (auto i = 0; i <  sx * sy; ++i) {
      int val = img->pixels[i];
      img->pixels[i] = (uint8_t)(maxValue * (double)cumulativeHisto.histogram[val] / (double)(sx * sy));
    }

  }

  void
  balance(rgb24_image* img) {

    gray8_image* r = img->extractChannel(0);
    gray8_image* g = img->extractChannel(1);
    gray8_image* b = img->extractChannel(2);

    balance(r);
    balance(g);
    balance(b);

    auto& bufferR = r->get_buffer();
    auto& bufferG = g->get_buffer();
    auto& bufferB = b->get_buffer();

    int sx = img->sx;
    int sy = img->sy;
    for (auto i = 0; i < sx * sy; ++i) {
        img->pixels[i * 3] = bufferR[i];
        img->pixels[i * 3 + 1] = bufferG[i];
        img->pixels[i * 3 + 2] = bufferB[i];
    }

    delete r;
    delete g;
    delete b;

  }

}
