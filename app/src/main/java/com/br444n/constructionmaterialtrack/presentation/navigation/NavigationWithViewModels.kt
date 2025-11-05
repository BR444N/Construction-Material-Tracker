package com.br444n.constructionmaterialtrack.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.br444n.constructionmaterialtrack.presentation.screens.add_material.AddMaterialScreen
import com.br444n.constructionmaterialtrack.presentation.screens.add_project.AddProjectScreen
import com.br444n.constructionmaterialtrack.presentation.screens.pdf_preview.PdfPreviewScreen
import com.br444n.constructionmaterialtrack.presentation.screens.project_details.ProjectDetailsScreen
import com.br444n.constructionmaterialtrack.presentation.screens.project_list.ProjectListScreen
import com.br444n.constructionmaterialtrack.presentation.screens.add_material.AddMaterialViewModel
import com.br444n.constructionmaterialtrack.presentation.screens.add_project.AddProjectViewModel
import com.br444n.constructionmaterialtrack.presentation.screens.pdf_preview.PdfPreviewViewModel
import com.br444n.constructionmaterialtrack.presentation.screens.project_details.ProjectDetailsViewModel
import com.br444n.constructionmaterialtrack.presentation.screens.project_list.ProjectListViewModel
import com.br444n.constructionmaterialtrack.presentation.features.settings.SettingsScreen
import com.br444n.constructionmaterialtrack.presentation.features.settings.SettingsViewModel
import com.br444n.constructionmaterialtrack.ui.theme.ThemeManager

sealed class Screen(val route: String) {
    object ProjectList : Screen("project_list")
    object AddProject : Screen("add_project")
    object Settings : Screen("settings")
    object AddMaterial : Screen("add_material/{projectId}?materialId={materialId}") {
        fun createRoute(projectId: String) = "add_material/$projectId"
        fun createEditRoute(projectId: String, materialId: String) = "add_material/$projectId?materialId=$materialId"
    }
    object ProjectDetails : Screen("project_details/{projectId}") {
        fun createRoute(projectId: String) = "project_details/$projectId"
    }
    object PdfPreview : Screen("pdf_preview/{projectId}") {
        fun createRoute(projectId: String) = "pdf_preview/$projectId"
    }
}

/**
 * Example of how navigation would work with ViewModels
 * This shows the proper MVVM architecture implementation
 */
@Composable
fun ArchitectProjectNavigationWithViewModels(
    navController: NavHostController = rememberNavController(),
    themeManager: ThemeManager
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
                    navController.navigate(Screen.Settings.route)
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
                },
                onNavigateToProjectDetails = { projectId ->
                    // Navegar directamente a ProjectDetailsScreen cuando se guarda el proyecto
                    navController.navigate(Screen.ProjectDetails.createRoute(projectId)) {
                        // Reemplazar AddProject en el back stack
                        popUpTo(Screen.AddProject.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = viewModel { SettingsViewModel(themeManager) }
            
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onThemeChanged = { isDarkTheme ->
                    themeManager.updateTheme(isDarkTheme)
                }
            )
        }
        
        composable(
            route = Screen.AddMaterial.route,
            arguments = listOf(
                androidx.navigation.navArgument("projectId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("materialId") { 
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val materialId = backStackEntry.arguments?.getString("materialId")
            val viewModel: AddMaterialViewModel = viewModel()
            
            AddMaterialScreen(
                viewModel = viewModel,
                projectId = projectId,
                materialId = materialId,
                onBackClick = {
                    navController.popBackStack()
                },
                onMaterialSaved = {
                    if (materialId != null) {
                        // Si estamos editando, solo regresamos a ProjectDetails
                        navController.popBackStack()
                    } else {
                        // Si estamos agregando, navegar a ProjectDetailsScreen después de guardar el material
                        navController.navigate(Screen.ProjectDetails.createRoute(projectId)) {
                            // Limpiar el back stack hasta AddProject para evitar duplicados
                            popUpTo(Screen.AddProject.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }
        
        composable(
            route = Screen.ProjectDetails.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
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
                },
                onEditMaterial = { projectId, materialId ->
                    navController.navigate(Screen.AddMaterial.createEditRoute(projectId, materialId))
                },
                onExportToPdf = { projectId ->
                    navController.navigate(Screen.PdfPreview.createRoute(projectId))
                }
            )
        }
        
        composable(Screen.PdfPreview.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val viewModel: PdfPreviewViewModel = viewModel()
            
            PdfPreviewScreen(
                viewModel = viewModel,
                projectId = projectId,
                onBackClick = {
                    navController.popBackStack()
                },
                onPdfGenerated = {
                    // Show success message or handle PDF generation completion
                }
            )
        }
    }
}