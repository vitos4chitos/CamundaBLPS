package main.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangeAnswer implements Serializable {
    private Long id;
    private String text;
    private String oldText;
    private String pageName;
    private String login;
}
