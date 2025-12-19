package com.back.domain.post.comment.document;

import com.back.global.BaseDocument;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "comments")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseDocument<String> {

    @Field(type = FieldType.Keyword)
    private String postId;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String author;

    public Comment(String postId, String content, String author) {
        this.postId = postId;
        this.content = content;
        this.author = author;
    }
}
