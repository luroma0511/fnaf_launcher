#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

uniform vec2 shadowOffset;
uniform float shadowSmoothing;
uniform vec4 shadowColor;
uniform float textureSize;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
    float smoothing = 1.0/textureSize;
    float distance = texture2D(u_texture, v_texCoord).a;
    float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
    vec4 text = vec4(v_color.rgb, v_color.a * alpha);

    float shadowDistance = texture2D(u_texture, v_texCoord - shadowOffset).a;
    float shadowAlpha = smoothstep(0.5 - shadowSmoothing, 0.5 + shadowSmoothing, shadowDistance);
    vec4 shadow = vec4(shadowColor.rgb, shadowColor.a * shadowAlpha);

    gl_FragColor = mix(shadow, text, text.a);
}