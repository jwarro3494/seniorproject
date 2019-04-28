# seniorproject
Modified MLKit Quickstart App for attempting to automate the process of congenital muscular torticollis testing.

Under the direct supervision of Dr. Tamirat Abegaz, I was tasked with modifying an older version of the MLKit Quickstart app in order to test the possibility of performing congenital muscular torticollis testing done completely within an android application. To obtain this goal, three face detection results had to be captured for the child's eyes and the eyes of two face stickers placed on the child's left and right shoulders. Key landmarks on the faces including the left and right eyes were used to calculate an intersection point for which a triangle could be made. Using the triangle created, we were able to calculate an intersection angle between lines drawn from the child's face and the two faces placed on the shoulders of the child using the law of cosines. In the end, the project failed due to the lower face stickers being difficult at times to detect.

For more details, please read the project report.
Also, this app was simply a proof of concept and nothing more. 
