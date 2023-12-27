package com.korea.Team5.movie;

import com.korea.Team5.Review.Review;
import com.korea.Team5.Review.ReviewService;
import com.korea.Team5.USER.Member;
import com.korea.Team5.USER.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/movie")
public class MovieController {
    private final MovieService movieService;
    private final MemberService memberService;
    private final ReviewService reviewService;
    @GetMapping("/list")
    public String list(Model model) {
        List<Movie> movieList = this.movieService.list();
        model.addAttribute("movieList", movieList);
        return "movieList";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model,  @RequestParam(value="page", defaultValue="0") int page ,@PathVariable("id") Integer id) {
        Movie movie = this.movieService.getMovie(id);
        Page<Review> paging = this.reviewService.getList(page);
        model.addAttribute("movie", movie);
        model.addAttribute("paging",paging);

        return "movieDetail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String movieVote(Principal principal, @PathVariable("id") Integer id){
        Movie movie = this.movieService.getMovie(id);
        Member member = this.memberService.getMember(principal.getName());

        this.movieService.vote(movie, member);

        return "redirect:/movie/list";
    }


}

