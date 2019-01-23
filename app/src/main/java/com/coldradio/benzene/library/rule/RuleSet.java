package com.coldradio.benzene.library.rule;

import com.coldradio.benzene.compound.Compound;

import java.util.ArrayList;
import java.util.List;

public class RuleSet {
    private static RuleSet msInstance = new RuleSet();
    private List<ICompoundRule> mRuleSet = new ArrayList<>();

    private boolean isRegistered(ICompoundRule compoundRule) {
        for (ICompoundRule rule : mRuleSet) {
            if (rule == compoundRule) {
                return true;
            }
        }
        return false;
    }

    public RuleSet() {
        add(new LetteringIfNotSeenRule());
        add(new LetteringIfNotCarbonRule());
        add(new AdjustDoubleBondTypeRule());
    }

    public static RuleSet instance() {
        return msInstance;
    }

    public void add(ICompoundRule compoundRule) {
        if (! isRegistered(compoundRule)) {
            mRuleSet.add(compoundRule);
        }
    }

    public Compound apply(Compound compound) {
        for (ICompoundRule rule : mRuleSet) {
            rule.apply(compound);
        }
        return compound;
    }

    public List<Compound> apply(List<Compound> compounds) {
        for (Compound c : compounds)
            apply(c);

        return compounds;
    }

    public List<Compound> apply(List<Compound> compounds, ICompoundRule rule) {
        for (Compound c : compounds)
            rule.apply(c);

        return compounds;
    }
}
