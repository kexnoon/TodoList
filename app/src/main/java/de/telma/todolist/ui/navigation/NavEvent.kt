package de.telma.todolist.ui.navigation

import android.app.Activity
import android.net.Uri
import androidx.navigation.NavController

sealed interface ComposableNavEvent {
    fun execute(navController: NavController)
}

sealed interface ActivityNavEvent {
    fun execute(activity: Activity)
}

sealed class NavEvent {
    data class ToComposeScreen private constructor(
        val destination: Destination? = null,
        val deeplink: Uri? = null
    ): NavEvent(), ComposableNavEvent {
        constructor(destination: Destination): this(destination, null)
        constructor(deeplink: Uri): this(null, deeplink)

        override fun execute(navController: NavController) {
            if (destination != null)
                navController.navigate(destination)
            else
                deeplink?.let { navController.navigate(deeplink) }
        }
    }

    data class PopTo(
        val destination: Destination,
        val isInclusive: Boolean = true
    ): NavEvent(), ComposableNavEvent {
        override fun execute(navController: NavController) {
            navController.popBackStack(destination, isInclusive)
        }
    }

    data object PopBack: NavEvent(), ComposableNavEvent {
        override fun execute(navController: NavController) {
            navController.popBackStack()
        }
    }

    data class Toast(
        val text: String = "",
        val length: Length = Length.Short
    ): NavEvent(), ActivityNavEvent {

        override fun execute(activity: Activity) {
            android.widget.Toast.makeText(activity, text, length.i).show()
        }

        enum class Length(val i: Int) {
            Short(android.widget.Toast.LENGTH_SHORT),
            Long(android.widget.Toast.LENGTH_LONG)
        }
    }
}