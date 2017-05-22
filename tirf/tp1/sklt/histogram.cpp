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

    for (auto i = 1; i < IMAGE_MAX_LEVEL; ++i) {
      result.histogram[i] = result.histogram[i - 1] + histogram.histogram[i];
    }

    return result;

  }

}
