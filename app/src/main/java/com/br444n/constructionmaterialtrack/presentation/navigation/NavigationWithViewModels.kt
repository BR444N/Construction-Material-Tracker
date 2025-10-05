package com.br444n.constructionmaterialtrack.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.br444n.constructionmaterialtrack.presentation.screens.add_material.AddMaterialScreen
import com.br444n.constructionmaterialtrack.presentation.screens.add_project.AddProjectScreen
import com.br444n.constructionmaterialtrack.presentation.screens.project_details.ProjectDetailsScreen
import com.br444n.constructionmaterialtrack.presentation.screens.project_list.ProjectListScreen
import com.br444n.constructionmaterialtrack.presentation.screens.add_material.AddMaterialViewModel
import com.br444n.constructionmaterialtrack.presentation.screens.add_project.AddProjectViewModel
import com.br444n.constructionmaterialtrack.presentation.screens.project_details.ProjectDetailsViewModel
import com.br444n.constructionmaterialtrack.presentation.screens.project_list.ProjectListViewModel

sealed class Screen(val route: String) {
    object ProjectList : Screen("project_list")
    object AddProject : Screen("add_project")
    object AddMaterial : Screen("add_material/{projectId}") {
        fun createRoute(projectId: String) = "add_material/$projectId"
    }
    object ProjectDetails : Screen("project_details/{projectId}") {
        fun createRoute(projectId: String) = "project_details/$projectId"
    }
}

/**
 * Example of how navigation would work with ViewModels
 * This shows the proper MVVM architecture implementation
 */
@Composable
fun ArchitectProjectNavigationWithViewModels(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ProjectList.route
    ) {
        composable(Screen.ProjectList.route) {
            val viewModel: ProjectListViewModel = viewModel()
            
            ProjectListScreen(
                viewModel = viewModel,
                onAddProject = {
                    navController.navigate(Screen.AddProject.route)
                },
                onProjectClick = { project ->
                    navController.navigate(Screen.ProjectDetails.createRoute(project.id))
                },
                onSettingsClick = {
                    // TODO: Navigate to settings screen
                }
            )
        }
        
        composable(Screen.AddProject.route) {
            val viewModel: AddProjectViewModel = viewModel()
            
            AddProjectScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddMaterialsClick = { projectId ->
                    navController.navigate(Screen.AddMaterial.createRoute(projectId))
                },
                onProjectSaved = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.AddMaterial.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val viewModel: AddMaterialViewModel = viewModel()
            
            AddMaterialScreen(
                viewModel = viewModel,
                projectId = projectId,
                onBackClick = {
                    navController.popBackStack()
                },
                onMaterialSaved = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.ProjectDetails.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val viewModel: ProjectDetailsViewModel = viewModel()
            
            ProjectDetailsScreen(
                viewModel = viewModel,
                projectId = projectId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddMaterial = { projectId ->
                    navController.navigate(Screen.AddMaterial.createRoute(projectId))
                }
            )
        }
    }
}