(function () {
    "use strict";

    window.onload = function () { 
        var result = document.getElementById("result"),
            canvas = result.getContext("2d"),
            width = result.width,
            height = result.height,
            imageData = canvas.createImageData(width, height),
            spheres,
            light;


        function Sphere(pos, r) {
            this.pos = pos;
            this.r = r;
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

        function difference_vector(v1, v2) {
            return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
        }

        function max(x, y) {
            return (x>y?x:y);
        }

        function min(x, y) {
            return (x<y?x:y);
        }
        
        function closestIntersectionPoint(r_org, s) {
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


        function trace(r) {
            var i, sphere, smallestK, k, intersectionPoint, normal, dirToLight, intensity;

            smallestK = Infinity;

            for (i = 0; i < spheres.length; i++) {
                sphere = spheres[i];
                k = closestIntersectionPoint(r, spheres[i]);
                smallestK = min(smallestK, k);
            }
            if (smallestK === Infinity) {
                return [60, 60, 60];
            } else {
                intersectionPoint = new Vector3(r.org.x + smallestK * r.dir.x, r.org.y + smallestK * r.dir.y, r.org.z + smallestK * r.dir.z);
                normal = difference_vector(intersectionPoint, sphere.pos);
                normal.normalise();

                dirToLight = difference_vector(light.pos, intersectionPoint);
                dirToLight.normalise();

                intensity = dot_product(normal, dirToLight);
                
                return [max(0, 255*intensity), max(0, 200*intensity), max(0, 255*intensity)];
            }
            
        }

        function setPixelToColor(_imageData, x, y, r, g, b) {
            var i = (x + y * _imageData.width) * 4;
            _imageData.data[i] = r;
            _imageData.data[i+1] = g;
            _imageData.data[i+2] = b;
            _imageData.data[i+3] = 255;
        }

        function setPixelToArray(_imageData, x, y, a) {
            setPixelToColor(_imageData, x, y, a[0], a[1], a[2]);
        }


        spheres = [new Sphere(new Vector3(-120, 20, -1000), 110),
                new Sphere(new Vector3(100, -40, -1000), 90)];
        light = new Light(new Vector3(0, 800, 0));

        var field_of_view = 30;
        var a = Math.tan((field_of_view/2.0) * (Math.PI/180.0));
        var y, x;
        var r;
        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                r = new Ray(new Vector3(0, 0, 0), new Vector3(
                            ((x / width) * 2 - 1) * a * 1, 
                            (((y / height) * 2 - 1) * (-1)) * a, 
                            -1));

                setPixelToArray(imageData, x, y, trace(r));
            }
        }

        canvas.putImageData(imageData, 0, 0);
    };
}());
