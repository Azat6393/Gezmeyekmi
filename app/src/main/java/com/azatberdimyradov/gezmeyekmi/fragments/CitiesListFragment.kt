package com.azatberdimyradov.gezmeyekmi.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.azatberdimyradov.gezmeyekmi.R
import com.azatberdimyradov.gezmeyekmi.adapters.CitiesListAdapter
import com.azatberdimyradov.gezmeyekmi.data.City
import com.azatberdimyradov.gezmeyekmi.data.SortOrder
import com.azatberdimyradov.gezmeyekmi.databinding.FragmentCitiesListBinding
import com.azatberdimyradov.gezmeyekmi.utils.onQueryTextChanged
import com.azatberdimyradov.gezmeyekmi.viewmodel.CitiesListViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CitiesListFragment : Fragment(R.layout.fragment_cities_list),
    CitiesListAdapter.OnItemClickListener {

    private val viewModel: CitiesListViewModel by viewModels()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCitiesListBinding.bind(view)
        val cityAdapter = CitiesListAdapter(this)

        binding.apply {
            citiesListRecyclerView.apply {
                adapter = cityAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val city = cityAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onCitySwipe(city)
                }
            }).attachToRecyclerView(binding.citiesListRecyclerView)
        }

        viewModel.cities.observe(viewLifecycleOwner) {
            cityAdapter.submitList(it)
            cityAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.citiesEvent.collect { event ->
                when (event) {
                    is CitiesListViewModel.CitiesEvent.ShowUndoDeleteCityMessage -> {
                        Snackbar.make(requireView(), "City deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                viewModel.onUndoDeleteClick(event.city, event.pictures)
                            }.setActionTextColor(resources.getColor(R.color.white))
                            .show()
                    }
                }
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cities_nav_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged {
            //update search query
            viewModel.searchQuery.value = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(city: City) {
        val action =
            CitiesListFragmentDirections.actionListFragmentToDetailsFragment(city, city.name)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchView.setOnQueryTextListener(null)
    }
}