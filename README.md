# seniorproject
Modified MLKit Quickstart App for attempting to automate the process of congenital muscular torticollis testing.

Under the direct supervision of Dr. Tamirat Abegaz, I was tasked with modifying an older version of the MLKit Quickstart app in order to test the possibility of performing congenital muscular torticollis testing done completely within an android application. To obtain this goal, three face detection results had to be captured for the child's eyes and the eyes of two face stickers placed on the child's left and right shoulders. Key landmarks on the faces including the left and right eyes were used to calculate an intersection point for which a triangle could be made. Using the triangle created, we were able to calculate an intersection angle between lines drawn from the child's face and the two faces placed on the shoulders of the child using the law of cosines. In the end, the project failed due to the lower face stickers being difficult at times to detect.

For more details, please read the project report.
Also, this app was simply a proof of concept and nothing more. 

License: 

Copyright 2018 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Modified by Jamie Arrowood-Forrester for Dr. Tamirat Abegaz under the
apache 2.0 license above.
