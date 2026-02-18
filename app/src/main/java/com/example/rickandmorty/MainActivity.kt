package com.example.rickandmorty

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rickandmorty.ui.screens.all_episodes.AllEpisodesViewModel
import com.example.rickandmorty.ui.CharacterViewModel
import com.example.rickandmorty.ui.EpisodeViewModel
import com.example.rickandmorty.ui.screens.search.SearchViewModel
import com.example.rickandmorty.ui.component.ErrorScreen
import com.example.rickandmorty.ui.component.LoadingState
import com.example.rickandmorty.ui.screens.all_episodes.AllEpisodesScreen
import com.example.rickandmorty.ui.screens.details.CharacterDetailsScreen
import com.example.rickandmorty.ui.screens.character_episode.CharacterEpisodeScreen
import com.example.rickandmorty.ui.screens.home.HomeScreen
import com.example.rickandmorty.ui.screens.home.HomeScreenViewModel
import com.example.rickandmorty.ui.screens.search.SearchScreen
import com.example.rickandmorty.ui.theme.RickAction
import com.example.rickandmorty.ui.theme.RickAndMortyTheme
import com.example.rickandmorty.ui.theme.RickPrimary
import com.example.rickandmorty.ui.utils.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    sealed class NavDestination(val title: String, val route: String, val icon: ImageVector) {
        object Home : NavDestination("Home", "home_screen", icon = Icons.Filled.Home)
        object Episodes :
            NavDestination("Episodes", "episodes_screen", icon = Icons.Filled.PlayArrow)

        object Search : NavDestination("Search", "search_screen", icon = Icons.Filled.Search)

    }

    companion object {
        private const val TAG = "MainActivity"
    }

    private val characterViewModel: CharacterViewModel by viewModels()
    private val episodeViewModel: EpisodeViewModel by viewModels()
    private val allEpisodesViewModel: AllEpisodesViewModel by viewModels()

    private val homeScreenViewModel: HomeScreenViewModel by viewModels()

    private val searchViewModel: SearchViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            // استدعاء API أول ما الشاشة تفتح
//            LaunchedEffect(Unit) {
//                characterViewModel.getCharacter(1)   // مثلا page = 1
//            }

            val navController = rememberNavController()

            val items = listOf(
                NavDestination.Home,
                NavDestination.Episodes,
                NavDestination.Search
            )

            @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
            RickAndMortyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(containerColor = RickPrimary) {

                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(imageVector = screen.icon,
                                            modifier = Modifier.size(30.dp),
                                            contentDescription = null)
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    selected = currentDestination?.route == screen.route,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                            Log.d(TAG, "onCreate: ${screen.route}")

                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent,
                                        selectedIconColor = RickAction,
                                        selectedTextColor = RickAction,
                                        // أضف هذه الأسطر ⬇️
                                        unselectedIconColor = Color.LightGray,  // أو أي لون تريده
                                        unselectedTextColor = Color.LightGray   // أو أي لون تريده
                                    )
                                )
                            }


                        }
                    }
                ) { innerPadding ->


                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(RickPrimary)
                            .padding(innerPadding)
                    ) {

                        val episodesListState = rememberLazyListState()  // ← hoisted هنا


                        NavHost(
                            navController = navController,
                            startDestination = "home_screen"
                        ) {

                            composable(route = "home_screen") {
                                HomeScreen(homeScreenViewModel) { characterId ->
                                    navController.navigate("character_details/$characterId")

                                    Log.d(TAG, "onCreate: characterId $characterId")
                                }
                            }

                            composable(
                                route = "character_details/{characterId}",
                                arguments = listOf(
                                    navArgument("characterId") {
                                        type = NavType.IntType
                                    }
                                )) { backStackEntry ->

                                val characterId =
                                    backStackEntry.arguments?.getInt("characterId") ?: -1

                                CharacterDetailsScreen(
                                    viewModel = characterViewModel,
                                    characterId = characterId,
                                    episodesClicked = { episodeIds ->
                                        val idsString =
                                            episodeIds.joinToString(",") // حولناها "1,2,3"

                                        navController.navigate("character_episodes/$idsString")
                                    },
                                    onBackClicked = { navController.navigateUp() }
                                )
                            }

                            composable(
                                route = "character_episodes/{episodeIds}",
                                arguments = listOf(navArgument("episodeIds") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val idsString =
                                    backStackEntry.arguments?.getString("episodeIds") ?: ""
                                val ids = idsString.split(",").mapNotNull { it.toIntOrNull() }

                                val characterState =
                                    characterViewModel.character.collectAsState().value

                                when (characterState) {
                                    is Resource.Success -> {
                                        val character = characterState.data
                                        CharacterEpisodeScreen(
                                            character,
                                            ids,
                                            episodeViewModel,
                                            episodesListState
                                        )
                                    }

                                    is Resource.Error -> ErrorScreen(characterState.message)
                                    is Resource.Loading, is Resource.Initial -> LoadingState()
                                    Resource.Empty -> ErrorScreen("Character not found")
                                }
                            }

                            composable(route = NavDestination.Episodes.route) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AllEpisodesScreen(allEpisodesViewModel)

                                }
                            }
                            composable(route = NavDestination.Search.route) {
//                                Column(
//                                    modifier = Modifier.fillMaxSize(),
//                                    verticalArrangement = Arrangement.Center,
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//                                    Text(
//                                        "Search Screen",
//                                        fontSize = 32.sp,
//                                        color = Color.White,
//                                        textAlign = TextAlign.Center,
//                                        modifier = Modifier.fillMaxWidth()
//                                    )
//                                }

                                SearchScreen(searchViewModel){charId->
                                    navController.navigate("character_details/$charId")
                                }
                            }


                        }
                    }
                }
            }
        }
    }

    @Composable
    fun EpisodesScreen(charId: Int) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Character episode screen : $charId", fontSize = 32.sp, color = RickAction)
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        RickAndMortyTheme {
            Greeting("Android")
        }
    }
}
