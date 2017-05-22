 #include "sklt/image_io.hh"
 #include "sklt/image.hh"
 #include "sklt/histogram.hh"

int main(int argc, char** argv) {

  if (argc < 2) return 1;

  auto img = tirf::load_image(*(argv + 1));
  //auto imgGrayscale = new tirf::gray8_image(img);

  //balance(imgGrayscale);
  balance(img);

  tirf::save_image(*img, "balance-color.tga");

  delete img;

}