/*
 * Cloud9: A MapReduce Library for Hadoop
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.umd.cloud9.io.pair;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import edu.umd.cloud9.io.WritableComparatorUtils;

/**
 * WritableComparable representing a pair consisting of a String and a float.
 * The elements in the pair are referred to as the left and right elements. The
 * natural sort order is: first by the left element, and then by
 * the right element.
 *
 * @author Ferhan Ture
 */
public class PairOfStringFloat implements WritableComparable<PairOfStringFloat> {

	private String leftElement;
	private float rightElement;

	/**
	 * Creates a pair.
	 */
	public PairOfStringFloat() {}

	/**
	 * Creates a pair.
	 *
	 * @param left the left element
	 * @param right the right element
	 */
	public PairOfStringFloat(String left, float right) {
		set(left, right);
	}

	/**
	 * Deserializes the pair.
	 *
	 * @param in source for raw byte representation
	 */
	public void readFields(DataInput in) throws IOException {
		leftElement = in.readUTF();
		rightElement = in.readFloat();
	}

	/**
	 * Serializes this pair.
	 *
	 * @param out where to write the raw byte representation
	 */
	public void write(DataOutput out) throws IOException {
		out.writeUTF(leftElement);
		out.writeFloat(rightElement);
	}

	/**
	 * Returns the left element.
	 *
	 * @return the left element
	 */
	public String getLeftElement() {
		return leftElement;
	}

	/**
	 * Returns the right element.
	 *
	 * @return the right element
	 */
	public float getRightElement() {
		return rightElement;
	}

	/**
	 * Returns the key (left element).
	 *
	 * @return the key
	 */
	public String getKey() {
		return leftElement;
	}

	/**
	 * Returns the value (right element).
	 *
	 * @return the value
	 */
	public float getValue() {
		return rightElement;
	}

	/**
	 * Sets the right and left elements of this pair.
	 *
	 * @param left the left element
	 * @param right the right element
	 */
	public void set(String left, float right) {
		leftElement = left;
		rightElement = right;
	}

	/**
	 * Checks two pairs for equality.
	 *
	 * @param obj object for comparison
	 * @return <code>true</code> if <code>obj</code> is equal to this object, <code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		PairOfStringFloat pair = (PairOfStringFloat) obj;
		return leftElement.equals(pair.getLeftElement()) && rightElement == pair.getRightElement();
	}

	/**
	 * Defines a natural sort order for pairs. Pairs are sorted first by the
	 * left element, and then by the right element.
	 *
	 * @return a value less than zero, a value greater than zero, or zero if
	 *         this pair should be sorted before, sorted after, or is equal to
	 *         <code>obj</code>.
	 */
	public int compareTo(PairOfStringFloat pair) {
		String pl = pair.getLeftElement();
		float pr = pair.getRightElement();

		if (leftElement.equals(pl)) {
			if (rightElement == pr)
				return 0;

			return rightElement < pr ? -1 : 1;
		}

		return leftElement.compareTo(pl);
	}

	/**
	 * Returns a hash code value for the pair.
	 *
	 * @return hash code for the pair
	 */
	@Override
	public int hashCode() {
		return leftElement.hashCode() + (int) rightElement;
	}

	/**
	 * Generates human-readable String representation of this pair.
	 *
	 * @return human-readable String representation of this pair
	 */
	@Override
	public String toString() {
		return "(" + leftElement + ", " + rightElement + ")";
	}

	/**
	 * Clones this object.
	 *
	 * @return clone of this object
	 */
	@Override
	public PairOfStringFloat clone() {
		return new PairOfStringFloat(this.leftElement, this.rightElement);
	}

	/** Comparator optimized for <code>PairOfStringFloat</code>. */
	public static class Comparator extends WritableComparator {

		/**
		 * Creates a new Comparator optimized for <code>PairOfStringFloat</code>.
		 */
		public Comparator() {
			super(PairOfStringFloat.class);
		}

		/**
		 * Optimization hook.
		 */
		public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
			String thisLeftValue = WritableComparatorUtils.readUTF(b1, s1);
			String thatLeftValue = WritableComparatorUtils.readUTF(b2, s2);

			if (thisLeftValue.equals(thatLeftValue)) {
				int s1offset = readUnsignedShort(b1, s1);
				int s2offset = readUnsignedShort(b2, s2);

				float thisRightValue = readFloat(b1, s1 + 2 + s1offset);
				float thatRightValue = readFloat(b2, s2 + 2 + s2offset);

				return (thisRightValue < thatRightValue ? -1
						: (thisRightValue == thatRightValue ? 0 : 1));
			}

			return thisLeftValue.compareTo(thatLeftValue);
		}
	}

	static { // register this comparator
		WritableComparator.define(PairOfStringFloat.class, new Comparator());
	}
}
