package com.zavgar.system.parcelable

actual interface CommonSerializable

actual interface CommonParcelable : CommonSerializable

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.PROPERTY)
actual annotation class CommonIgnoredOnParcel
