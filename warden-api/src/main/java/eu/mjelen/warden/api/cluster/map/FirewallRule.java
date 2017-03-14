package eu.mjelen.warden.api.cluster.map;

import java.util.HashSet;
import java.util.Set;

public class FirewallRule {

    private String name;

    private Set<String> sourceTags = new HashSet<>();
    private Set<String> sourceRanges = new HashSet<>();
    private Set<String> targetTags = new HashSet<>();
    private Set<String> targetRanges = new HashSet<>();

    private Set<String> allow = new HashSet<>();
    private Set<String> deny = new HashSet<>();

    public void setName(String name) {
        this.name = name;
    }

    public void addSourceTag(String tag) {
        this.sourceTags.add(tag);
    }

    public void addSourceRange(String range) {
        this.sourceRanges.add(range);
    }

    public void addTargetTag(String tag) {
        this.targetTags.add(tag);
    }

    public void addTargetRange(String range) {
        this.targetRanges.add(range);
    }

    public void addAllow(String allow) {
        this.allow.add(allow);
    }

    public void addDeny(String deny) {
        this.deny.add(deny);
    }

    public String getName() {
        return name;
    }

    public Set<String> getSourceTags() {
        return sourceTags;
    }

    public Set<String> getSourceRanges() {
        return sourceRanges;
    }

    public Set<String> getTargetTags() {
        return targetTags;
    }

    public Set<String> getTargetRanges() {
        return targetRanges;
    }

    public Set<String> getAllow() {
        return allow;
    }

    public Set<String> getDeny() {
        return deny;
    }
}
