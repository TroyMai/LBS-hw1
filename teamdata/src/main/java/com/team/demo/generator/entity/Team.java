package com.team.demo.generator.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Team {
    private String teamName;
    private int winNum;
    private List<Integer> conclude = new ArrayList<>();

    public void resetConclude()
    {
        conclude = new ArrayList<>();
    }
}
