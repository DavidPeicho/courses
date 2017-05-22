 #include "sklt/image_io.hh"
 #include "sklt/image.hh"

int main(int argc, char** argv) {

  if (argc < 2) return 1;

  //save_image(rgb24_image &image, const char *filename);
  auto img = tirf::load_image(*(argv + 1));
  auto imgGrayscale = new tirf::gray8_image(img);

  auto result = new tirf::rgb24_image(imgGrayscale);
  tirf::save_image(*result, "test.tga");

  delete img;
  delete imgGrayscale;
  delete result;

}