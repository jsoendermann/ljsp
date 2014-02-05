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
            width_org,
            height,
            height_org,
            imageData,
            spheres,
            planes,
            light;



        function Sphere(pos, r, clr, refl) {
            this.pos = pos;
            this.r = r;
            this.clr = clr;
            this.refl = refl;
        }

        function Plane(n, d, clr, refl) {
            n.normalise();
            this.n = n;
            this.d = d;
            this.clr = clr;
            this.refl = refl;
        }

        function Light(pos) {
            this.pos = pos;
        }

        function vector_length(v) {
            return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        }

        function vector_distance(v1, v2) {
            return vector_length(difference_vector(v1, v2));
        }

        function Vector3(x, y, z) {
            this.x = x;
            this.y = y;
            this.z = z;

            this.normalise = function() {
                var length = vector_length(this);
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

        function follow_ray(r, k) {
            r.dir.normalise();
            return new Vector3(r.org.x + k*r.dir.x, r.org.y + k*r.dir.y, r.org.z + k*r.dir.z);
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
        
        function rayPlaneIntersectionPoint(r, p) {
            var t, n, d;

            d = dot_product(r.dir, p.n);

            if (Math.abs(d) < 0.1)
                return Infinity;
            
            n = -p.d - dot_product(r.org, p.n);

            t = n/d;

            return t;
            


        }
        function raySphereIntersectionPoint(r_org, s) {
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
            t1 = (-B + Math.sqrt(discriminant)) / (2.0 * A);


            if (t0 > t1) {
                var temp = t0;
                t0 = t1;
                t1 = temp;
            }

            if (t1 < 0)
                return Infinity;

            if (t0 < 0)
                return t1;

            return t0;

        }

        
        // returns [k, point, object]
        function closestIntersectionPoint(r) {
            var smallestK = Infinity, closestObject = null, sphere, plane;
            
            // spheres
            for (i = 0; i < spheres.length; i++) {
                sphere = spheres[i];
                k = raySphereIntersectionPoint(r, sphere);
                // this happens when the ray starts on a ray, ignore that intersection
                if (k < 0.1) {
                    continue;
                }
                if (k < smallestK) {
                    smallestK = k;
                    closestObject = sphere;
                }
            }

            // planes
            for (i = 0; i < planes.length; i++) {
                plane = planes[i];
                k = rayPlaneIntersectionPoint(r, plane);

                if (k < 0.1) {
                    continue;
                }

                if (k < smallestK) {
                    smallestK = k;
                    closestObject = plane;
                }
            }


            if (smallestK === Infinity)
                return [Infinity, null, null];
            else
                // TODO use follow_ray
                return [smallestK, follow_ray(r, smallestK), closestObject];
        }


//var c= 20;

        function compute_normal(p, obj) {
            var normal;
            if (obj.constructor.name == "Plane") {
                normal = obj.n;
            } else if (obj.constructor.name == "Sphere") {
                normal = difference_vector(p, obj.pos);
            } else {
                console.log("cannot compute normal for obj of type "+obj.constructor.name);
            }
            normal.normalise();

            return normal;

        }

        function trace(r, recursion_depth) {
            var i, sphere, closestObject, k, intersectionPoint, normal, dirToLight, intensity, reflectionVector, reflectedRay, reflectedColour;

            closest = closestIntersectionPoint(r);
            k = closest[0];
            intersectionPoint = closest[1];
            closestObject = closest[2];


            if (k === Infinity) {
                return new Colour(0, 0, 0); //Colour(60, 60, 60);
            } else {
                if (k < 0) console.log(k);
                intersectionPoint = new Vector3(r.org.x + k * r.dir.x, r.org.y + k * r.dir.y, r.org.z + k * r.dir.z);
                normal = compute_normal(intersectionPoint, closestObject);
                normal.normalise();

                dirToLight = difference_vector(light.pos, intersectionPoint);
                dirToLight.normalise();

                var shadowRay = new Ray(intersectionPoint, dirToLight);

                

                closestPointBetweenLight = closestIntersectionPoint(shadowRay);

                //if (c > 0 && closestSphere === spheres[0] && closestPointBetweenLight[0] !== Infinity) {
                //    console.log(closestPointBetweenLight);
                //    console.log(vector_distance(closestPointBetweenLight[1], closestPointBetweenLight[2].pos));//vector_length(difference_vector(closestPointBetweenLight[1], closestPointBetweenLight[2].pos)));
                //    console.log(vector_distance(closestPointBetweenLight[2].pos, follow_ray(shadowRay, -closestPointBetweenLight[0])));
                //    c -= 1;
                //}


                if (closestPointBetweenLight[0] !== Infinity && vector_distance(intersectionPoint, light.pos) > vector_distance(intersectionPoint, closestPointBetweenLight[1]))
                    intensity = 0;
                else {

                    intensity = dot_product(normal, dirToLight);// * (1/vector_distance(intersectionPoint, light.pos));
                }

                // ambient light
                intensity = max(intensity, 0.2);

                //intensity *= 2;

                if (recursion_depth > 0 && closestObject.refl) {
                    r.dir.normalise();
     

                    reflectionVector = difference_vector(r.dir, scala_vector_product(2 * dot_product(r.dir, normal), normal));
                    reflectionVector.normalise();

                    reflectedRay = new Ray(intersectionPoint, reflectionVector);

                    reflectedColour = trace(reflectedRay, recursion_depth - 1);
    
                    //console.log(reflectedColour);
                    //if (reflectedColour.r != 0 || reflectedColour.g != 0 || reflectedColour.b != 0)
                        return add_colours_with_weights(0.5, reflectedColour, 0.5, scaled_colour(intensity, closestObject.clr));
                    //else
                    //    return new Colour(255, 200, 255);



                } else {
                return scaled_colour(intensity, closestObject.clr);
                }
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


        spheres = [new Sphere(new Vector3(-50, -40, 300), 25, new Colour(255, 0, 0), true),
                new Sphere(new Vector3(35, -40, 300), 50, new Colour(0, 255, 0), true),
                new Sphere(new Vector3(-40, 30, 300), 25, new Colour(0, 0, 255), true)];
        planes = [new Plane(new Vector3(0, -1, 0), 60, new Colour(255, 255, 255), false),
                new Plane(new Vector3(0, 0, -1), 400, new Colour(255, 255, 255), false),
                new Plane(new Vector3(1, 0, 0), 110, new Colour(255, 255, 255), false),
                new Plane(new Vector3(-1, 0, 0), 120, new Colour(255, 255, 255), false),
                new Plane(new Vector3(0, 1, 0), 110, new Colour(255, 255, 255), false),
                new Plane(new Vector3(0, 0, 1), 5, new Colour(255, 255, 255), false)];
        light = new Light(new Vector3(0, 0, 180));

        window.onload = function () { 
        var canvas_result = document.getElementById("result");
        var canvas_twice = document.getElementById("result_twice");
        context_res = canvas_result.getContext("2d");
        var context_twice = canvas_twice.getContext("2d");
        width_org = canvas_result.width;
        height_org = canvas_result.height;
        width = canvas_twice.width;
        height = canvas_twice.height;
        
        imageData = context_twice.createImageData(width, height);


        
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

        context_twice.putImageData(imageData, 0, 0);

        context_res.scale(0.5,0.5);
        context_res.drawImage(canvas_twice, 0, 0);

        //context.scale(0.5, 0.5);
        //context.putImageData(imageData, 0, 0);
        }
//    };
//}());
