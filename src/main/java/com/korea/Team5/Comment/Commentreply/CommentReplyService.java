package com.korea.Team5.Comment.Commentreply;

import com.korea.Team5.Comment.Comment;
import com.korea.Team5.DataNotFoundException;
import com.korea.Team5.USER.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentReplyService {


  private final CommentReplyRepository commentReplyRepository;



  public void create(Member member, String content, Comment comment){
    CommentReply reply = new CommentReply();
    reply.setContent(content);
    reply.setCreateDate(LocalDateTime.now());
    reply.setMember(member);
    reply.setComment(comment);

    this.commentReplyRepository.save(reply);
  }
  public CommentReply getreply(Integer replyId){
    Optional<CommentReply> reply = this.commentReplyRepository.findById(replyId);
    if (reply.isPresent()) {
      return reply.get();
    } else {
      throw new DataNotFoundException("reply not found");
    }

  }
  public void delete(CommentReply commentReply){

    this.commentReplyRepository.delete(commentReply);
  }
  public void modify(CommentReply commentReply,String content){
    commentReply.setContent(content);
    commentReply.setModifyDate(LocalDateTime.now());
    this.commentReplyRepository.save(commentReply);
  }
  public boolean vote(CommentReply commentReply, Member member){
    Set<Member> voters = commentReply.getVoter();

    // 이미 추천한 상태이면 취소하고 false 반환
    if (voters.contains(member)) {
      voters.remove(member);
      this.commentReplyRepository.save(commentReply);
      return false;
    }
    // 추천되지 않은 상태이면 추가하고 true 반환
    voters.add(member);
    this.commentReplyRepository.save(commentReply);
    return true;
  }

}
