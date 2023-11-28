package com.gamelink.gamelinkapp.view

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.gamelink.gamelinkapp.R
import com.gamelink.gamelinkapp.databinding.ActivityCommunityBinding
import com.gamelink.gamelinkapp.view.adapter.ViewPagerCommunityAdapter
import com.gamelink.gamelinkapp.viewmodel.CommunityViewModel
import com.google.android.material.tabs.TabLayout

class CommunityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommunityBinding
    private lateinit var viewModel: CommunityViewModel
    private lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommunityBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(CommunityViewModel::class.java)

        setContentView(binding.root)

        bundle = intent.extras!!

        binding.imageBack.setOnClickListener {
            finish()
        }

        binding.buttonJoinCommunity.setOnClickListener {
            val communityId = bundle.getString("community_id").toString()

            viewModel.join(communityId)
        }

        binding.buttonLeaveCommunity.setOnClickListener {
            val communityId = bundle.getString("community_id").toString()

            viewModel.leave(communityId)
        }

        binding.imageCommunityOptions.setOnClickListener {
            showPopupMenu(it)
        }

        setViewPager()

        observe()
    }

    override fun onResume() {
        super.onResume()
        loadDataFromActivity()
    }

    private fun loadDataFromActivity() {
        val communityId = bundle.getString("community_id").toString()

        viewModel.load(communityId)

    }

    private fun observe() {
        viewModel.community.observe(this) {
            binding.textName.text = it.name
            binding.textDescription.text = it.description
            Glide.with(this).load(it.bannerUrl).into(binding.imageBanner)

            viewModel.isOwner(it.ownerId)
        }

        viewModel.userIsOwner.observe(this) {
            if (it) {
                binding.buttonJoinCommunity.visibility = View.GONE
                binding.buttonLeaveCommunity.visibility = View.GONE
            } else {
                binding.imageCommunityOptions.visibility = View.GONE
                val communityId = bundle.getString("community_id").toString()

                viewModel.joined(communityId)
            }
        }

        viewModel.joined.observe(this) {
            if (it) {
                binding.buttonJoinCommunity.visibility = View.GONE
                binding.buttonLeaveCommunity.visibility = View.VISIBLE
            } else {
                binding.buttonLeaveCommunity.visibility = View.GONE
                binding.buttonJoinCommunity.visibility = View.VISIBLE
            }
        }

        viewModel.delete.observe(this) {
            if(it.status()) {
                finish()
                Toast.makeText(this, "Comunidade apagada com sucesso", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setViewPager() {
        val tabLayout = binding.tabLayoutCommunity
        val viewPager = binding.viewPagerCommunity

        val viewPagerCommunityAdapter = ViewPagerCommunityAdapter(this)
        viewPagerCommunityAdapter.setCommunityId(bundle.getInt("community_id"))

        viewPager.adapter = viewPagerCommunityAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // TODO("Not yet implemented")
            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.getTabAt(position)?.select()
            }
        })
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.options_community_menu, this.menu)
        }

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_update_community -> {
                    val intent = Intent(this, CommunityFormActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    true
                }
                R.id.item_delete_community -> {
                    showAlertDialog(bundle.getString("community_id")!!)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showAlertDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle("Apagar Comunidade")
            .setMessage("Deseja apagar comunidade?")
            .setPositiveButton("Sim") { _, _ ->
                viewModel.delete(id)
            }
            .setNeutralButton("Cancelar", null)
            .show()
    }
}