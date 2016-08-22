package org.string_db.search;

/**
 * Represents one search hit.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class ProteinHit {
    private Long id;
    private Float score;
    private String highlighted;

    public ProteinHit() {
    }

    public ProteinHit(Long proteinId, Float score, String highlighted) {
        this.id = proteinId;
        this.score = score;
        this.highlighted = highlighted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long proteinId) {
        this.id = proteinId;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getHighlighted() {
        return highlighted;
    }

    public void setHighlighted(String highlighted) {
        this.highlighted = highlighted;
    }

    @Override
    public String toString() {
        return "ProteinHit{" +
                "id=" + id +
                ", score=" + score +
                ", highlighted='" + highlighted + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProteinHit that = (ProteinHit) o;

        if (highlighted != null ? !highlighted.equals(that.highlighted) : that.highlighted != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (score != null ? !score.equals(that.score) : that.score != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (highlighted != null ? highlighted.hashCode() : 0);
        return result;
    }
}
