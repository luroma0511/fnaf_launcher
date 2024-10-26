attribute vec4 a_color;
attribute vec3 a_position;
attribute vec2 a_texCoord0;

varying vec2 v_texCoord;
varying vec4 v_color;

uniform mat4 u_projTrans;

void main(){
    v_color = a_color;
    v_texCoord = a_texCoord0;
    gl_Position = u_projTrans * vec4(a_position, 1);
}