//************************************************
//*                                              *
//*   TP 1 TIRF (c) 2017 J. FABRIZIO             *
//*                                              *
//*                               LRDE EPITA     *
//*                                              *
//************************************************

#include "image.hh"
#include <cstdlib>

namespace tirf {

gray8_image::gray8_image(int _sx, int _sy) {
    sx = _sx;
    sy = _sy;

    length = sx*sy;
    pixels = (GRAY8)aligned_alloc(TL_IMAGE_ALIGNMENT, length);
}

gray8_image::gray8_image(const rgb24_image* colorImg)
            : gray8_image(colorImg->sx, colorImg->sy) {

    auto& buffer = colorImg->get_buffer();
    for (auto i = 0; i < sx * sy; ++i) {
        auto r = buffer[i * 3];
        auto g = buffer[i * 3 + 1];
        auto b = buffer[i * 3 + 2];
        pixels[i] = (uint8_t)((double)r * 0.299 + (double)g * 0.587 + (double)b * 0.114);
    }

}

gray8_image::~gray8_image() {
  free(pixels);
}

const GRAY8& gray8_image::get_buffer() const {
    return pixels;
}

GRAY8& gray8_image::get_buffer() {
    return pixels;
}

rgb24_image::rgb24_image(int _sx, int _sy) {
    sx = _sx;
    sy = _sy;

    length = sx*sy*3;
    pixels = (RGB8)aligned_alloc(TL_IMAGE_ALIGNMENT, length);
}

rgb24_image::rgb24_image(const gray8_image* grayImg)
            : rgb24_image(grayImg->sx, grayImg->sy) {

    auto& buffer = grayImg->get_buffer();
    for (auto i = 0; i < length; i += 3) {
        auto val = buffer[i / 3];
        pixels[i] = val;
        pixels[i + 1] = val;
        pixels[i + 2] = val;
    }

}

rgb24_image::rgb24_image(const gray8_image* r, const gray8_image* g,
                         const gray8_image* b)
            : rgb24_image(r->sx, r->sy) {

    auto& bufferR = r->get_buffer();
    auto& bufferG = g->get_buffer();
    auto& bufferB = b->get_buffer();
    for (auto i = 0; i < sx * sy; ++i) {
        pixels[i * 3] = bufferR[i];
        pixels[i * 3 + 1] = bufferG[i];
        pixels[i * 3 + 2] = bufferB[i];
    }

}

rgb24_image::~rgb24_image() {
  free(pixels);
}

gray8_image* rgb24_image::extractChannel(int channel) {

    gray8_image* result = new gray8_image(sx, sy);

    for (auto i = 0; i < sx * sy; ++i) {
        result->pixels[i] = pixels[i * 3 + channel];
    }
    return result;

}

const RGB8& rgb24_image::get_buffer() const {
    return pixels;
}

RGB8& rgb24_image::get_buffer() {
    return pixels;
}


}
