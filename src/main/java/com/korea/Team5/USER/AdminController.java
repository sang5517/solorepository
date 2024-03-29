package com.korea.Team5.USER;

import com.korea.Team5.DataNotFoundException;
import com.korea.Team5.Notice.Notice;
import com.korea.Team5.Notice.NoticeCreateform;
import com.korea.Team5.Notice.NoticeService;
import com.korea.Team5.Review.Review;
import com.korea.Team5.Review.ReviewService;
import com.korea.Team5.movie.MovieService;
import com.korea.Team5.movie.entity.Movie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private final MemberService memberService;
  private final ReviewService reviewService;
  private final MovieService movieService;
  private final NoticeService noticeService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/adminpage")
  public String adminpage() {


    return "/ADMIN/admin_signform";
  }


  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/adminsignup")
  public String adminsignup(Model model, @RequestParam String loginId) {
    try {
      // DB에서 해당 loginId에 대한 Member를 찾아옵니다.
      Member memberFromDB = memberService.getMember(loginId);
      // DB에 해당 loginId가 존재할 경우의 로직
      // DB에 해당 loginId가 존재할 경우의 로직
      model.addAttribute("validSearch", true);
      model.addAttribute("memberExists", true);
      model.addAttribute("memberFromDB", memberFromDB);

      return "/ADMIN/admin_signform"; // 일치하는 경우

    } catch (DataNotFoundException ex) {
      // DB에 해당 loginId가 존재하지 않을 경우의 로직
      model.addAttribute("errorMessage", "해당 아이디가 존재하지 않습니다.");
      model.addAttribute("validSearch", true);
      model.addAttribute("memberExists", false);

      return "/ADMIN/admin_signform";


    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/admindelete")
  public String admindelete(@RequestParam String loginId, Model model) {
    try {
      // 서비스를 통해 해당 회원 삭제
      memberService.deleteMember(loginId);

      model.addAttribute("deleteform", true);


      // 삭제가 성공했을 경우, 다시 검색 폼으로 이동
      return "redirect:/admin/adminpage";
    } catch (DataNotFoundException e) {
      // 데이터를 찾을 수 없는 경우의 예외 처리
      model.addAttribute("errorMessage", "해당 아이디가 존재하지 않습니다.");

      return "/ADMIN/admin_signform"; // 예를 들어, notfound 뷰로 이동하거나 다른 방식으로 처리 가능


    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/adminreview")
  public String adminreview(@RequestParam String loginId, Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
    model.addAttribute("loginId", loginId);
    try {
      Member member = memberService.getMember(loginId);
      // 로그인한 멤버가 작성한 리뷰 목록을 가져옴
      Page<Review> paging = this.reviewService.getListReveiwMember(page, member);
      // 가져온 리뷰 목록이 비어 있는지 확인
      if (paging.isEmpty()) {
        model.addAttribute("error", "작성한 리뷰가 존재하지 않습니다.");

        Member memberFromDB = memberService.getMember(loginId);
        model.addAttribute("memberFromDB", memberFromDB);
        model.addAttribute("validSearch", true);
        model.addAttribute("memberExists", true);
        return "/ADMIN/admin_signform";
      }
      // 가져온 리뷰 목록을 모델에 추가
      model.addAttribute("paging", paging);
      return "/ADMIN/admin_signform";
    } catch (DataNotFoundException e) {
      // 데이터를 찾을 수 없는 경우의 예외 처리
      model.addAttribute("error", "아이디가 존재하지 않습니다.");
      return "/ADMIN/admin_signform";
    }

  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/adminreivewdelete")
  public String adminreivewdelete(@RequestParam Integer reviewId, @RequestParam String loginId) {

    // reviewId를 기반으로 Review를 검색합니다.
    Review review = reviewService.findReviewById(reviewId);
    reviewService.delete(review);

    return "redirect:/admin/adminreview?loginId=" + loginId;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/adminsignlist")
  public String adminsignlist(Model model, @RequestParam(defaultValue = "true") boolean toggleVisible, @RequestParam(value = "page", defaultValue = "0") int page) {
    // getAllMembers 메서드를 사용하여 모든 멤버를 가져옴
    Page<Member> signpaging = this.memberService.getAllMembers(page);
    // Model에 페이징 정보 추가
    model.addAttribute("signpaging", signpaging);
    model.addAttribute("toggleVisible", toggleVisible);
    // 데이터가 없을 경우 처리
    if (signpaging.isEmpty()) {
      model.addAttribute("error", "조회된 회원이 없습니다.");
    }
    return "/ADMIN/admin_signform";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/adminmovielist")
  public String adminmovielist(Model model, @RequestParam(value = "page", defaultValue = "0") int page, Movie movie) {

    Page<Movie> adminmoviespaging = this.movieService.getAllMovies(page);
    model.addAttribute("adminmoviespaging", adminmoviespaging);
    if (adminmoviespaging.isEmpty()) {
      model.addAttribute("error", "조회된 영화가 없습니다.");
    }
    return "/ADMIN/admin_movielist";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/adminmoviedelete")
  public String adminmoviedelete(@RequestParam Integer movie) {

    // reviewId를 기반으로 Review를 검색합니다.
    Movie movie1 = movieService.getMovie(movie);
    movieService.delete(movie1);


    return "redirect:/admin/adminmovielist";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/adminsigndetail")
  public String adminsigndetail(Model model, @RequestParam String loginId) {

    Member member = memberService.getMember(loginId);
    model.addAttribute("member", member);


    return "/ADMIN/admindetail_form";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/check")
  public String checkDataBase() {
    return "movie";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/notice/create")
  public String create(Model model, NoticeCreateform noticeCreateform, Member member) {

    model.addAttribute("adminLoginId", member.getLoginId());


    return "/Notice/notice_create";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/notice/create")
  public String create(Model model, @Valid NoticeCreateform noticeCreateform,
                       Principal principal) {

    Member member = this.memberService.getMember(principal.getName());
    this.noticeService.create(noticeCreateform.getSubject(), noticeCreateform.getContent(),
            member);
    model.addAttribute("member", member);

    return "redirect:/notice/list";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/notice/delete")
  public String delete(@RequestParam Integer noticeId) {
    Notice notice = this.noticeService.getNotice(noticeId);
    this.noticeService.delete(notice);
    return "redirect:/notice/list";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/notice/modify")
  public String modify(Model model, @RequestParam Integer noticeId, @RequestParam String newsubject, String newcontent, RedirectAttributes redirectAttributes) {
    Notice notice = this.noticeService.getNotice(noticeId);
    if (notice != null) {
      this.noticeService.modify(notice, newsubject, newcontent);
      redirectAttributes.addAttribute("noticeId", noticeId);
      return "redirect:/notice/detail";
    } else {
      model.addAttribute("error", "공지사항이 존재하지 않습니다.");
      return "redirect:/notice/list";
    }
  }
}
