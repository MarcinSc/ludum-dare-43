#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform LOWP vec4 u_color;
uniform float u_time;

varying vec2 v_texCoords;
varying vec2 v_screenPosition;

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
float noise(vec3 p) {

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
    float noise = (noise(vec3(v_texCoords*3.0 + v_screenPosition*5.0, u_time/4.0))+1.0)/2.0;
    float distanceFromCenter = clamp(distance(v_texCoords, vec2(0.5, 0.5))*2.0, 0.0, 1.0);

    float minAlpha = 0.3;
    float alpha = clamp(-1.0/(1.0-minAlpha)*distanceFromCenter+1.0/(1.0-minAlpha), 0.0, 1.0);
    alpha *= clamp(1.5*noise, 0.0, 1.0);

    gl_FragColor = vec4(u_color.rgb, alpha);
}