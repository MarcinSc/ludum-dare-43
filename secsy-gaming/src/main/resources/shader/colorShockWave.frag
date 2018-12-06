#ifdef GL_ES
precision mediump float;
#endif

#define PI 3.1415926535897932384626433832795

uniform sampler2D u_sourceTexture;
uniform vec2 u_position;
uniform float u_distance;
uniform float u_size;
uniform float u_alpha;
uniform float u_heightToWidth;
uniform vec4 u_color;
uniform float u_noiseImpact;
uniform float u_noiseVariance;

varying vec2 v_position;

vec3 random3(vec3 c) {
    float j = 4096.0*sin(dot(c,vec3(17.0, 59.4, 15.0)));
    vec3 r;
    r.z = fract(512.0*j);
    j *= .125;
    r.x = fract(512.0*j);
    j *= .125;
    r.y = fract(512.0*j);
    return r-0.5;
}

const float F3 =  0.3333333;
const float G3 =  0.1666667;
float snoise(vec3 p) {

    vec3 s = floor(p + dot(p, vec3(F3)));
    vec3 x = p - s + dot(s, vec3(G3));

    vec3 e = step(vec3(0.0), x - x.yzx);
    vec3 i1 = e*(1.0 - e.zxy);
    vec3 i2 = 1.0 - e.zxy*(1.0 - e);

    vec3 x1 = x - i1 + G3;
    vec3 x2 = x - i2 + 2.0*G3;
    vec3 x3 = x - 1.0 + 3.0*G3;

    vec4 w, d;

    w.x = dot(x, x);
    w.y = dot(x1, x1);
    w.z = dot(x2, x2);
    w.w = dot(x3, x3);

    w = max(0.6 - w, 0.0);

    d.x = dot(random3(s), x);
    d.y = dot(random3(s + i1), x1);
    d.z = dot(random3(s + i2), x2);
    d.w = dot(random3(s + 1.0), x3);

    w *= w;
    w *= w;
    d *= w;

    return dot(d, vec4(52.0));
}

void main() {
    vec2 pixelPosition = v_position - 0.5;
    pixelPosition.y *= u_heightToWidth;
    float distance = distance(pixelPosition, u_position);

    vec2 normalVectorFromCenter = normalize(pixelPosition - u_position);

    if (u_noiseImpact > 0.0) {
        distance += u_noiseImpact * snoise(vec3(normalVectorFromCenter * u_noiseVariance, 0.0));
    }

    if (u_distance-u_size < distance && distance < u_distance + u_size) {
        // We're inside the shockwave
        if (u_alpha >= 1.0) {
            gl_FragColor = u_color;
        } else {
            gl_FragColor = mix(texture2D(u_sourceTexture, v_position), u_color, u_alpha);
        }
    } else {
        gl_FragColor = texture2D(u_sourceTexture, v_position);
    }
}