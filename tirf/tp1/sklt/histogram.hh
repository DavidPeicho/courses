//************************************************
//*                                              *
//*   TP 1 TIRF (c) 2017 J. FABRIZIO             *
//*                                              *
//*                               LRDE EPITA     *
//*                                              *
//************************************************

#ifndef HISTOGRAM_HH
#define	HISTOGRAM_HH

#include "image.hh"

namespace tirf {

  typedef struct { unsigned int histogram[IMAGE_NB_LEVELS]; } histogram_1d;

  histogram_1d computeHistogram(const gray8_image* img);

  histogram_1d cumulativeHistogram(const histogram_1d& histogram);

}

#endif
