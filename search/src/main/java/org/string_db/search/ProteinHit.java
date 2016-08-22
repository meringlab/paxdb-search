package org.string_db.search;

/**
 * Represents one search hit.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class ProteinHit {
    private Long id;
    private String name;
    private String annotation;
    private Float score;
    private String highlighted;

    public ProteinHit() {
    }

    public ProteinHit(Long proteinId, Float score, String highlighted) {
        this.id = proteinId;
        this.score = score;
        this.highlighted = highlighted;
    }

    public ProteinHit(Long id, String name, String annotation, Float score, String highlighted) {
        this.id = id;
        this.name = name;
        this.annotation = annotation;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
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

        return getId().equals(that.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
