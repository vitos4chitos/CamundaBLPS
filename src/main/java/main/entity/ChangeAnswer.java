package main.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangeAnswer {
    private Long id;
    private String text;
    private String oldText;
    private String pageName;
    private String login;
}
