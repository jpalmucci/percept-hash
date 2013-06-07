percept-hash
============

Simple java perceptual hash over text.

To use:

1. Create a PerceptualHash object
2. Train it on a large sample of the text you want to hash
3. Optinally serialize it to disk so you don't have to retrain
4. Call hash to get an integer value that should be the same for very similar strings. Use as many bits as you need to safely avoid collisions, but too many could cause similar strings to hash to different values.

