//************************************************
//*                                              *
//*   TP 1 TIRF (c) 2017 J. FABRIZIO             *
//*                                              *
//*                               LRDE EPITA     *
//*                                              *
//************************************************


#ifndef IMAGE_IO_HH
#define	IMAGE_IO_HH

#include "image.hh"

namespace tirf {

  bool save_image(rgb24_image &image, const char *filename);
  rgb24_image *load_image(const char* filename);

}

#endif
