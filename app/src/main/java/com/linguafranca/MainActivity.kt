package com.linguafranca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.linguafranca.domain.repository.UserRepository
import com.linguafranca.ui.navigation.LinguaFrancaNavHost
import com.linguafranca.ui.theme.LinguaFrancaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val darkTheme = userRepository.observeCurrentUser()
                .flatMapLatest { user ->
                    if (user != null) {
                        userRepository.observeUserSettings(user.id)
                            .map { it?.darkTheme }
                    } else {
                        flowOf(null)
                    }
                }
                .collectAsState(initial = null)

            LinguaFrancaTheme(
                darkTheme = darkTheme.value ?: isSystemInDarkTheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LinguaFrancaNavHost()
                }
            }
        }
    }
}

