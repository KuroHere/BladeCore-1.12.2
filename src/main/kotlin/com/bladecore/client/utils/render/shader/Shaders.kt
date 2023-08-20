package com.bladecore.client.utils.render.shader

object Shaders {
    private const val VERTEX_SHADER = "/assets/shaders/vertex.vsh"
    private const val VERTEX_SHADER_GUI = "/assets/shaders/vertexGui.vsh"

    val menuShader = FragmentShader("/assets/shaders/menu/main.fsh", VERTEX_SHADER)
    val rectShader = FragmentShader("/assets/shaders/gui/rect.frag", VERTEX_SHADER_GUI)
}