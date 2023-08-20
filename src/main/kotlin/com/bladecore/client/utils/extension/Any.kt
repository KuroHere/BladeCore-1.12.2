package com.bladecore.client.utils.extension

fun <T: Any> T.transformIf(flag: Boolean, block: (prev: T) -> T) =
    if (flag) block(this) else this