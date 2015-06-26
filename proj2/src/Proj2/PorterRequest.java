/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Proj2;

/**
 *
 * @author asus
 */
public class PorterRequest {
    private int id;
    private String answer;

    public PorterRequest() {
        id=-1;
        answer="";
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }
}
