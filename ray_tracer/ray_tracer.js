//(function () {
//    "use strict";
//
//    window.onload = function () { 
//      var result = document.getElementById("result"),
//            canvas = result.getContext("2d"),
//            width = result.width,
//            height = result.height,
//            imageData = canvas.createImageData(width, height),
//            spheres,
//            light;

        var result,
            canvas,
            width,
            height,
            imageData,
            spheres,
            light;



        function Sphere(pos, r, clr) {
            this.pos = pos;
            this.r = r;
            this.clr = clr;
        }

        function Light(pos) {
            this.pos = pos;
        }

        function Vector3(x, y, z) {
            this.x = x;
            this.y = y;
            this.z = z;

            this.normalise = function() {
                var length = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
                this.x /= length;
                this.y /= length;
                this.z /= length;
            };

            this.copy = function() {
                return new Vector3(this.x, this.y, this.z);
            };
        }
        
        function Colour(r, g, b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        function scaled_colour(s, c) {
            return new Colour(min(max(c.r*s, 0), 255), min(max(c.g*s, 0), 255), min(max(c.b*s, 0), 255));
        }

        function add_colours_with_weights(w1, c1, w2, c2) {
            return new Colour(w1*c1.r + w2*c2.r, w1*c1.g + w2*c2.g, w1*c1.b + w2*c2.b);
        }

        function Ray(org, dir) {
            this.org = org;
            this.dir = dir;
            this.dir.normalise();

            this.copy = function() {
                return new Ray(this.org.copy(), this.dir.copy());
            };
        }

        //TODO camel case names

        function dot_product(v1, v2) {
            return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
        }

        function cross_product(u, v) {
            return new Vector3(u.y*v.z - u.z*v.y, u.z*v.x - u.x*v.z, u.x*v.y - u.y*v.x);
        }
        
        function scala_vector_product(s, v) {
            return new Vector3(v.x * s, v.y * s, v.z * s);
        }

        function add_vectors(v1, v2) {
            return new Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
        }

        function difference_vector(v1, v2) {
            return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
        }

        function max(x, y) {
            return (x>y?x:y);
        }

        function min(x, y) {
            return (x<y?x:y);
        }
        
        function intersectionPoint(r_org, s) {
            var r, A, B, C, discriminant, t0, t1;

            // r_org in object space of s
            r = r_org.copy();
            r.org.x -= s.pos.x;
            r.org.y -= s.pos.y;
            r.org.z -= s.pos.z;

            A = dot_product(r.dir, r.dir);
            B = 2 * dot_product(r.dir, r.org);
            C = dot_product(r.org, r.org) - s.r*s.r;

            discriminant = B*B - 4 * A * C;

            if (discriminant < 0.0) {
                return Infinity;
            }

            t0 = (-B - Math.sqrt(discriminant)) / (2.0 * A);
            t1 = (B - Math.sqrt(discriminant)) / (2.0 * A);


            if (t0 < 0 && t1 < 0) {
                return Infinity;
            } else if (t0 < 0 || t1 < 0) {
                t0 = max(t0, t1);
            } else { //(t0 >= 0 && t1 >= 0)
                t0 = min(t0, t1);
            }

            return t0;
        }

        
        // returns [k, point, sphere]
        function closestIntersectionPoint(r) {
            var smallestK = Infinity, closestSphere = null;
            
            for (i = 0; i < spheres.length; i++) {
                sphere = spheres[i];
                k = intersectionPoint(r, sphere);
                if (k < smallestK) {
                    smallestK = k;
                    closestSphere = sphere;
                }
            }

            return [smallestK, add_vectors(r.org, scala_vector_product(smallestK, r.dir)), closestSphere];
        }



        function trace(r, recursion_depth) {
            var i, sphere, closestSphere, k, intersectionPoint, normal, dirToLight, intensity, reflectionVector, reflectedRay, reflectedColour;

            closest = closestIntersectionPoint(r);
            k = closest[0];
            intersectionPoint = closest[1];
            closestSphere = closest[2];

            //console.log(closest);

            if (k === Infinity) {
                return new Colour(0, 0, 0); //Colour(60, 60, 60);
            } else {
                intersectionPoint = new Vector3(r.org.x + k * r.dir.x, r.org.y + k * r.dir.y, r.org.z + k * r.dir.z);
                normal = difference_vector(intersectionPoint, closestSphere.pos);
                normal.normalise();

                dirToLight = difference_vector(light.pos, intersectionPoint);
                dirToLight.normalise();

                intensity = dot_product(normal, dirToLight);

                //if (recursion_depth > 0) {
                //    reflectionVector = difference_vector(r.dir, scala_vector_product(2 * dot_product(r.dir, normal), normal));
                //    reflectionVector.normalise();

                //    reflectedRay = new Ray(intersectionPoint, reflectionVector);

                //    reflectedColour = trace(reflectedRay, recursion_depth - 1);
    
                //    //console.log(reflectedColour);
                //    if (reflectedColour.r != 0 || reflectedColour.g != 0 || reflectedColour.b != 0)
                //        return add_colours_with_weights(0.5, reflectedColour, 0.5, new Colour(255, 200, 255));
                //    else
                //        return new Colour(255, 200, 255);



                //} else {
                    return scaled_colour(intensity, closestSphere.clr);
                //}
            }
            
        }

        function setPixelToRGB(_imageData, x, y, r, g, b) {
            var i = (x + y * _imageData.width) * 4;
            _imageData.data[i] = r;
            _imageData.data[i+1] = g;
            _imageData.data[i+2] = b;
            _imageData.data[i+3] = 255;
        }


        function setPixelToColour(_imageData, x, y, c) {
            setPixelToRGB(_imageData, x, y, c.r, c.g, c.b);
        }


        spheres = [new Sphere(new Vector3(0, 0, 300), 20, new Colour(255, 200, 255)),
                new Sphere(new Vector3(100, -40, 300), 30, new Colour(200, 255, 255)),
                new Sphere(new Vector3(-100, 30, 300), 25, new Colour(255, 255, 200))];
        light = new Light(new Vector3(0, 400, 0));

        window.onload = function () { 
        result = document.getElementById("result");
        canvas = result.getContext("2d");
        width = result.width;
        height = result.height;
        imageData = canvas.createImageData(width, height);

        var x, y, r;
        var cameraDirection, cameraUp, cameraRight;
        var normalised_x, normalised_y;
        var t1, t2, t3, ray_dir;

        cameraDirection = new Vector3(0, 0, 1);
        cameraUp = new Vector3(0, 1, 0);
        cameraRight = cross_product(cameraDirection, cameraUp);

        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                normalised_x = (x/width)-0.5;
                normalised_y = (y/height)-0.5;

                t1 = scala_vector_product(normalised_x, cameraRight);
                t2 = scala_vector_product(normalised_y, cameraUp);
                t3 = add_vectors(t1, t2);
                ray_dir = add_vectors(t3, cameraDirection);

                r = new Ray(new Vector3(0, 0, 0), ray_dir);


                setPixelToColour(imageData, x, y, trace(r, 1));
            }
        }

        canvas.putImageData(imageData, 0, 0);
        }
//    };
//}());
