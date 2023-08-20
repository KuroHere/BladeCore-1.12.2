package com.bladecore.client.setting

import com.bladecore.client.module.Module
import com.bladecore.client.setting.type.*
import java.awt.Color

/*SETTING REGISTERING*/

/*ENUM*/
fun <T: Enum<T>> Module.setting(
    name: String,
    value: T,
    visible: () -> Boolean = {true},
    description: String = ""
): EnumSetting<T> {
    val setting = EnumSetting(name, value, visibility = visible, description = description)
    settings.add(setting)
    return setting
}

/*DOUBLE*/
fun Module.setting(
    name: String,
    value: Double,
    min: Double,
    max: Double,
    step: Double,
    visible: () -> Boolean = {true},
    description: String = ""
): DoubleSetting {
    val setting = DoubleSetting(name, value, min, max, step, visibility = visible, description = description)
    settings.add(setting)
    return setting
}

/*Boolean*/
fun Module.setting(
    name: String,
    value: Boolean,
    visible: () -> Boolean = {true},
    description: String = ""
): BooleanSetting {
    val setting = BooleanSetting(name, value, visibility = visible, description = description)
    settings.add(setting)
    return setting
}

/*String*/
fun Module.setting(
    name: String,
    value: String,
    visible: () -> Boolean = {true},
    description: String = ""
): StringSetting {
    val setting = StringSetting(name, value, visibility = visible, description = description)
    settings.add(setting)
    return setting
}

/*Color*/
fun Module.setting(
    name: String,
    value: Color,
    visible: () -> Boolean = {true},
    description: String = ""
): ColorSetting {
    val setting = ColorSetting(name, value, visibility = visible, description = description)
    settings.add(setting)
    return setting
}

/*Unit*/
fun Module.setting(
    name: String,
    block: () -> Unit,
    visible: () -> Boolean = {true},
    description: String = ""
): UnitSetting {
    val setting = UnitSetting(name, block, visible, description = description)
    settings.add(setting)
    return setting
}

