/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.model;

import java.util.Objects;

/**
 *
 * @author Tim Smith
 */
public class Mfg {
    private int id;
    private String name;

    public Mfg() {
    }

    public Mfg(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
        hash = 67 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Mfg other = (Mfg) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
}
