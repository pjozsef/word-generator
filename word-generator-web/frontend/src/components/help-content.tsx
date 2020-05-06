import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        firstHeader: {
            marginTop: theme.spacing(-1.5)
        }
    }))

export default function HelpContent() {
    const classes = useStyles()
    return <div>
        <h4 className={classes.firstHeader}>Expression</h4>
        <p>This is the input that is used to generate words. It can contain:
            <ul>
                <li>Substitution rules</li>
                <li>Inline substitution rules</li>
                <li>Markov chain rules</li>
                <li>Plain text</li>
            </ul>
            Most rules require a set of words as input that can be provided under Categories.
        </p>
        <h4>Categories</h4>
        <p>Each category contains a list of words used by rules. Click the + button in the left panel to create a new category.
           Each line is considered a separate "word", though technically they can be multiple words on one line too.
           Lines are trimmed of whitespace, empty lines are ignored.
        </p>
        <h4>Rules</h4>
        <p>Rules are special expressions that are evaluated and are replaced by their results in the expression.</p>
        <h5>Substitution rule</h5>
        <p>Substitutes itself to a random word from the given category.<br />
           Example: #{"{categoryName}"}<br />
           Chooses a random word from the category 'categoryName'
           Example: #{"{c1+c2+c3}"}<br />
           Chooses a random word from either 'c1', 'c2' or 'c3' category.
        </p>
        <h5>Inline substitution rule</h5>
        <p>Similar to Substitution rule, but the possible choices are enumerated in the rule itself separated by a "|" character.<br />
           Example: #{"{a|b|c|d|e|xxx}"} will respectfully choose one.<br />
           Inline substitution rules are handy when only a few choices are available and it is 
           too cumbersome to create a caegory for that.</p>
        <h5>Markov chain rule</h5>
        <p>Uses a markov chain built from the words of the input category. Markov chains are random, 
           but generate sequences of characters that are of a similar distribution as the input words. 
           It basically means that the generated words are similar but not necessarily the same as the input.<br />
           Markov chain rules can have a multitude of constraints:
           <ul>
           <li>{"*{categoryName}"} - basic rule for 'categoryName' category</li>
           <li>{"*{categoryName#3}"} - 3rd order markov chain. The larger the number, 
           the more similar the results will be to the input words. Usually a value between 2 and 3 works just fine.
           Numbers larger than the length of the input words don't have any effect.</li>
           <li>{"*{cat1+cat2#3}"} - Supports merging multiple categories</li>
           <li>{"*{categoryName 4-8}"} - Results will be 4-8 characters long.</li>
           <li>{"*{categoryName 2-}"} - Results will be at least 2 characters long.</li>
           <li>{"*{categoryName -10}"} - Results will be at most 10 characters long.</li>
           <li>{"*{categoryName un**}"} - Results will start with 'un...'.</li>
           <li>{"*{categoryName **ed}"} - Results will end with '...ed'.</li>
           <li>{"*{categoryName un**ed}"} - Results will start/end with 'un...ed'</li>
           <li>{"*{categoryName un*ish*ed}"} - Results will start/end with 'un...ed' and contain the letters 'ish' somewhere in between.<br/>
           These constraints can be used together or separately</li>
           <li>{"*{categoryName !un**}"} - Results will not start with 'un...'. Negation applies to the other start-middle-end rules too.</li>
           <li>{"*{categoryName#2, 4-7, b**, !c**, *a* }"} - Multiple constraints can be defined, each separated with a ','</li>
           </ul></p>
    </div>
}