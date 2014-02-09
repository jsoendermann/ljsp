function render(enableAsmJs) { 
    "use strict";

    // ######## Global variables #########

    var spheres, planes, light;
    var bgColour;

    var hugeValue = 1000000.0, tinyValue = 0.1;


    // ######## Classes ##########

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

    function Vector3(x, y, z) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.normalise = function() {
            var length = vectorLength(this);
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


    function Colour(r, g, b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }


    function Light(pos) {
        this.pos = pos;
    }


    // ######## Vector functions #########

    function vectorLength(v) {
        return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }

    function scalarVectorProduct(s, v) {
        return new Vector3(v.x * s, v.y * s, v.z * s);
    }

    function vectorsDistance(v1, v2) {
        return vectorLength(vectorsDifference(v1, v2));
    }

    function vectorsSum(v1, v2) {
        return new Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    function vectorsDifference(v1, v2) {
        return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    function vectorsDotProduct(v1, v2) {
        if (enableAsmJs) {
            return module.vectorsDotProduct(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
        } else {
            return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
        }
    }

    function vectorsCrossProduct(u, v) {
        return new Vector3(u.y*v.z - u.z*v.y, u.z*v.x - u.x*v.z, u.x*v.y - u.y*v.x);
    }


    // ########### Colour functions ############

    function colourScaled(s, c) {
        return new Colour(Math.min(Math.max(c.r*s, 0), 255), Math.min(Math.max(c.g*s, 0), 255), Math.min(Math.max(c.b*s, 0), 255));
    }

    function coloursMix(w1, c1, w2, c2) {
        return new Colour(w1*c1.r + w2*c2.r, w1*c1.g + w2*c2.g, w1*c1.b + w2*c2.b);
    }


    // ########### Ray functions ############

    function rayFollow(r, k) {
        r.dir.normalise();
        return new Vector3(r.org.x + k*r.dir.x, r.org.y + k*r.dir.y, r.org.z + k*r.dir.z);
    }


    // ########### Functions on 3D objects ############

    function computeNormal(p, obj) {
        var normal;
        if (obj.constructor.name === "Plane") {
            normal = obj.n;
        } else if (obj.constructor.name === "Sphere") {
            normal = vectorsDifference(p, obj.pos);
        } else {
            throw ("cannot compute normal for obj of type "+obj.constructor.name);
        }

        normal.normalise();
        return normal;
    }


    // ########### Helper functions #############

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



    // ############ Intersection functions ##############

    function rayPlaneIntersectionPoint(r, p) {
        var d;

        d = vectorsDotProduct(r.dir, p.n);

        if (Math.abs(d) < tinyValue) {
            return hugeValue;
        }

        return (-p.d - vectorsDotProduct(r.org, p.n))/d;
    }

    function raySphereIntersectionPoint(r_original, s) {
        if (enableAsmJs) {
            return module.raySphereIntersectionPoint(r_original.org.x, r_original.org.y, r_original.org.z, r_original.dir.x, r_original.dir.y, r_original.dir.z, s.pos.x, s.pos.y, s.pos.z, s.r);
        }
        else {
            var r, A, B, C, discriminant, t0, t1;

            // r: r_org in object space of s
            r = r_original.copy();
            r.org.x -= s.pos.x;
            r.org.y -= s.pos.y;
            r.org.z -= s.pos.z;

            A = vectorsDotProduct(r.dir, r.dir);
            B = 2 * vectorsDotProduct(r.dir, r.org);
            C = vectorsDotProduct(r.org, r.org) - s.r*s.r;

            discriminant = B*B - 4 * A * C;

            if (discriminant < 0.0) {
                return hugeValue;
            }

            t0 = (-B - Math.sqrt(discriminant)) / (2.0 * A);
            t1 = (-B + Math.sqrt(discriminant)) / (2.0 * A);

            if (Math.max(t0, t1) < 0) {
                return hugeValue;
            }

            if (Math.min(t0, t1) < 0) {
                return Math.max(t0, t1);
            }

            return Math.min(t0, t1);
        }
    }

    // TODO return an object instead of an array
    // returns [k, point, object]
    function closestIntersectionPoint(r) {
        var i, sphere, k, smallestK = hugeValue, closestObject = null, plane;

        // Spheres
        for (i = 0; i < spheres.length; i++) {
            sphere = spheres[i];
            k = raySphereIntersectionPoint(r, sphere);

            // This happens when the ray starts on the sphere, ignore that intersection
            if (k < 0.1) {
                continue;
            }
            if (k < smallestK) {
                smallestK = k;
                closestObject = sphere;
            }
        }

        // Planes
        for (i = 0; i < planes.length; i++) {
            plane = planes[i];
            k = rayPlaneIntersectionPoint(r, plane);

            // This happens when the ray starts on the plane, ignore that intersection
            if (k < 0.1) {
                continue;
            }

            if (k < smallestK) {
                smallestK = k;
                closestObject = plane;
            }
        }

        return [smallestK, rayFollow(r, smallestK), closestObject];
    }


    // ########### Tracing function ############

    function trace(r, depth) {
        var closest, k, intersectionPoint, closestObject, normal, dirToLight, shadowRay, closestPointInDirOfLight, intensity, reflectionVector, reflectedRay, reflectedColour;

        closest = closestIntersectionPoint(r);
        k = closest[0];
        intersectionPoint = closest[1];
        closestObject = closest[2];


        if (Math.abs(k - hugeValue) < tinyValue) {
            return bgColour;
        } else {
            //intersectionPoint = new Vector3(r.org.x + k * r.dir.x, r.org.y + k * r.dir.y, r.org.z + k * r.dir.z);
            normal = computeNormal(intersectionPoint, closestObject);

            dirToLight = vectorsDifference(light.pos, intersectionPoint);
            dirToLight.normalise();

            shadowRay = new Ray(intersectionPoint, dirToLight);

            closestPointInDirOfLight = closestIntersectionPoint(shadowRay);

            // If shadow ray intersects nothing or the closest intersection is behind the light
            if (Math.abs(closestPointInDirOfLight[0] - hugeValue) < tinyValue || vectorsDistance(intersectionPoint, light.pos) < vectorsDistance(intersectionPoint, closestPointInDirOfLight[1])) {
                intensity = vectorsDotProduct(normal, dirToLight);
            } else {
                intensity = 0;
            }

            // Ambient light
            intensity = Math.max(intensity, 0.2);

            if (depth > 0 && closestObject.refl) {
                reflectionVector = vectorsDifference(r.dir, scalarVectorProduct(2 * vectorsDotProduct(r.dir, normal), normal));
                reflectedRay = new Ray(intersectionPoint, reflectionVector);

                reflectedColour = trace(reflectedRay, depth - 1);

                return coloursMix(0.3, reflectedColour, 0.7, colourScaled(intensity, closestObject.clr));
            } else {
                return colourScaled(intensity, closestObject.clr);
            }
        }

    }


    // ######## Main function ##########

    var start = new Date().getTime();

    spheres = [new Sphere(new Vector3(-50, -40, 250), 25, new Colour(255, 0, 0), true),
            new Sphere(new Vector3(35, -40, 300), 50, new Colour(0, 255, 0), true),
            new Sphere(new Vector3(-40, 30, 300), 25, new Colour(0, 0, 255), true),
            new Sphere(new Vector3(50, 30, 200), 30, new Colour(255, 0, 255), true)];

    planes = [new Plane(new Vector3(0, -1, 0), 60, new Colour(200, 200, 200), false),
           new Plane(new Vector3(0, 0, -1), 400, new Colour(200, 200, 200), false),
           new Plane(new Vector3(1, 0, 0), 110, new Colour(200, 200, 200), false),
           new Plane(new Vector3(-1, 0, 0), 120, new Colour(200, 200, 200), false),
           new Plane(new Vector3(0, 1, 0), 110, new Colour(200, 200, 200), false),
           new Plane(new Vector3(0, 0, 1), 5, new Colour(200, 200, 200), false)];
    light = new Light(new Vector3(0, 0, 180));

    bgColour = new Colour(0, 0, 0);

    var canvas_result = document.getElementById("result");
    var canvas_twice = document.getElementById("result_twice");
    var context_res = canvas_result.getContext("2d");
    var context_twice = canvas_twice.getContext("2d");
    var width = canvas_twice.width;
    var height = canvas_twice.height;

    var imageData = context_twice.createImageData(width, height);

    var x, y, r;
    var cameraDirection, cameraUp, cameraRight;
    var normalised_x, normalised_y;
    var t1, t2, t3, ray_dir;

    cameraDirection = new Vector3(0, 0, 1);
    cameraUp = new Vector3(0, 1, 0);
    cameraRight = vectorsCrossProduct(cameraDirection, cameraUp);

    for (y = 0; y < height; y++) {
        for (x = 0; x < width; x++) {
            normalised_x = (x/width)-0.5;
            normalised_y = (y/height)-0.5;

            t1 = scalarVectorProduct(normalised_x, cameraRight);
            t2 = scalarVectorProduct(normalised_y, cameraUp);
            t3 = vectorsSum(t1, t2);
            ray_dir = vectorsSum(t3, cameraDirection);

            r = new Ray(new Vector3(0, 0, 0), ray_dir);

            setPixelToColour(imageData, x, y, trace(r, 1));
        }
    }

    context_twice.putImageData(imageData, 0, 0);
    context_res.scale(0.5,0.5);
    context_res.drawImage(canvas_twice, 0, 0);
    context_res.scale(2, 2);

    var elapsed = new Date().getTime() - start;
    if (enableAsmJs) {
        document.getElementById("timeAsmJs").innerHTML = "Rendered using asm.js in " + (elapsed/1000.0) + " seconds.";
    } else {
        document.getElementById("timeStandard").innerHTML = "Rendered using standard JavaScript in " + (elapsed/1000.0) + " seconds.";
    }

}
