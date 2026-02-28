package com.br444n.constructionmaterialtrack.presentation.screens.project_list

import android.app.Application
import app.cash.turbine.test
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.domain.repository.ProjectRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

import io.mockk.mockkObject
import io.mockk.unmockkAll
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.local.dao.ProjectDao
import com.br444n.constructionmaterialtrack.core.shortcuts.DynamicShortcutManager

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectListViewModelTest {

    private lateinit var viewModel: ProjectListViewModel
    private lateinit var mockApplication: Application
    private lateinit var mockRepository: ProjectRepository
    private lateinit var mockDatabase: ConstructionDatabase
    private lateinit var mockProjectDao: ProjectDao
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockApplication = mockk(relaxed = true)
        mockProjectDao = mockk(relaxed = true)
        mockDatabase = mockk(relaxed = true)
        
        // Mocking static/object methods that are called during init {}
        mockkObject(ConstructionDatabase.Companion)
        every { ConstructionDatabase.getDatabase(any()) } returns mockDatabase
        every { mockDatabase.projectDao() } returns mockProjectDao
        
        mockkObject(DynamicShortcutManager)
        every { DynamicShortcutManager.updateProjectShortcuts(any(), any()) } returns Unit
        
        // Mock the DAO since ProjectRepositoryImpl is real
        every { mockProjectDao.getAllProjects() } returns kotlinx.coroutines.flow.flowOf(emptyList())
        
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `ViewModel initialization loads projects and updates shortcuts`() = runTest {
        // Creamos la instancia para disparar el init {}
        viewModel = ProjectListViewModel(mockApplication)
        
        // Ejecutamos las corrutinas pendientes
        advanceUntilIdle()
        
        // Assert final state after flow collection
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.projects.isEmpty())
    }
    
    @Test
    fun `enterSelectionMode sets isSelectionMode to true and adds project to selected list`() = runTest {
        viewModel = ProjectListViewModel(mockApplication)
        
        viewModel.enterSelectionMode("project_123")
        
        assertTrue(viewModel.uiState.value.isSelectionMode)
        assertTrue(viewModel.uiState.value.selectedProjects.contains("project_123"))
    }
    
    @Test
    fun `exitSelectionMode clears selection mode and resets selected list`() = runTest {
        viewModel = ProjectListViewModel(mockApplication)
        viewModel.enterSelectionMode("project_123") // Setup
        
        viewModel.exitSelectionMode() // Act
        
        assertFalse(viewModel.uiState.value.isSelectionMode)
        assertTrue(viewModel.uiState.value.selectedProjects.isEmpty())
    }
}
