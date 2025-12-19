
package com.back.domain.post.post.document;

import com.back.global.BaseDocument;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "posts")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Post extends BaseDocument<String> {
    @Field(type= FieldType.Text)
    private String title;
    @Field(type= FieldType.Text)
    private String content;
    @Field(type= FieldType.Keyword)
    private String author;


    public Post(String title, String content, String author){
        this.title = title;
        this.content = content;
        this.author = author;
    }

}


