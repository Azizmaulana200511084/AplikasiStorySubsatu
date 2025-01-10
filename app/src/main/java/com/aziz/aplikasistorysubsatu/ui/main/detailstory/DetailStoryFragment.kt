package com.aziz.aplikasistorysubsatu.ui.main.detailstory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import coil.imageLoader
import coil.request.ImageRequest
import com.aziz.aplikasistorysubsatu.data.remote.response.stories.Story
import com.aziz.aplikasistorysubsatu.databinding.FragmentDetailStoryBinding

class DetailStoryFragment : Fragment() {

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)

        binding.story = Story(
            createdAt = "",
            description = arguments?.getString("description") ?: "",
            id = arguments?.getString("id") ?: "",
            name = arguments?.getString("name") ?: "",
            photoUrl = arguments?.getString("photo_url") ?: ""
        )

        val request = ImageRequest.Builder(requireContext())
            .data(arguments?.getString("photo_url"))
            .target(
                onSuccess = {
                    startPostponedEnterTransition()
                },
                onError = {
                    startPostponedEnterTransition()
                }
            )
            .build()

        requireActivity().application.imageLoader.enqueue(request)

        binding.executePendingBindings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}