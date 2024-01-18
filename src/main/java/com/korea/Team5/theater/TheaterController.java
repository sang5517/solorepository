package com.korea.Team5.theater;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/theater")
public class TheaterController {

  private final TheaterService theaterService;

  @GetMapping("/information")
  public String information(Model model,String bigRegion,String smallRegion){
    // getExcelRegion 메서드를 사용하여 엑셀 데이터를 가져옴
    List<String> bigRegionlList = theaterService.getBigRegion();
    // 엑셀 데이터를 모델에 추가
    model.addAttribute("bigRegionlList", bigRegionlList);

    return "/Theater/theater_form";
  }


//  @GetMapping("/bigregion")
//  public String bigregion(Model model,@RequestParam String bigRegion,@RequestParam String smallRegion){
//
//    List<Excel> regionexcelList = this.theaterService.getExcelRegion(bigRegion, smallRegion);
//    model.addAttribute("region",regionexcelList);
//
//
//    return "/Theater/theater_form";
//  }






//  @GetMapping("/bigregion")
//  public String bigregion(Model model,@RequestParam String bigRegion,@RequestParam String smallRegion){
//
//    List<Excel> regionexcelList = this.theaterService.getExcelRegion(bigRegion, smallRegion);
//    model.addAttribute("region",regionexcelList);
//
//
//    return "/Theater/theater_form";
//  }

  @GetMapping("/detail")
  public String detail(Model model, @RequestParam String targetBigRegion, @RequestParam String targetSmallRegion) {
    List<Excel> regionList = theaterService.getExcelRegion(targetBigRegion, targetSmallRegion);

    // 필요한 작업 수행
    model.addAttribute("regionList", regionList);
    model.addAttribute("targetBigRegion", targetBigRegion);
    model.addAttribute("targetSmallRegion", targetSmallRegion);

    return "/Theater/theater_detail";
  }

  @GetMapping("/smallRegion")
  public String smallRegion(Model model, @RequestParam String targetBigRegion){
    List<String> smallList = this.theaterService.getTargetSmallRegion(targetBigRegion);
    List<String> bigList = this.theaterService.getBigRegion();
    model.addAttribute("smallList",smallList);
    model.addAttribute("bigRegionlList",bigList);
    model.addAttribute("bigRegion",targetBigRegion);

    return "/Theater/theater_form";
  }
}
