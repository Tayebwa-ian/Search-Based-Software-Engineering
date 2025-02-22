package de.uni_passau.fim.se2.sbse.suite_minimisation.examples;


import de.uni_passau.fim.se2.sbse.suite_minimisation.examples.dependencies.BytesRef;
import de.uni_passau.fim.se2.sbse.suite_minimisation.examples.dependencies.IndexableField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Documents are the unit of indexing and search.
 * <p>
 * A Document is a set of fields.  Each field has a name and a textual value. A field may be stored
 * with the document, in which case it is returned with search hits on the document.  Thus each
 * document should typically contain one or more stored fields which uniquely identify it.
 */
public final class Document implements Iterable<IndexableField> {

    private final static String[] NO_STRINGS = new String[0];
    private final List<IndexableField> fields = new ArrayList<>();

    /**
     * Constructs a new document with no fields.
     */
    public Document() {
    }

    @Override
    public Iterator<IndexableField> iterator() {
        return fields.iterator();
    }

    /**
     * <p>Adds a field to a document.  Several fields may be added with
     * the same name.  In this case, if the fields are indexed, their text is treated as though
     * appended for the purposes of search.</p>
     * <p> Note that add like the removeField(s) methods only makes sense
     * prior to adding a document to an index. These methods cannot be used to change the content of
     * an existing index! In order to achieve this, a document has to be deleted from an index and a
     * new changed version of that document has to be added.</p>
     */
    public final void add(IndexableField field) {
        fields.add(field);
    }

    /**
     * <p>Removes field with the specified name from the document.
     * If multiple fields exist with this name, this method removes the first field that has been
     * added. If there is no field with the specified name, the document remains unchanged.</p>
     * <p> Note that the removeField(s) methods like the add method only make sense
     * prior to adding a document to an index. These methods cannot be used to change the content of
     * an existing index! In order to achieve this, a document has to be deleted from an index and a
     * new changed version of that document has to be added.</p>
     */
    public final void removeField(String name) {
        Iterator<IndexableField> it = fields.iterator();
        while (it.hasNext()) {
            IndexableField field = it.next();
            if (field.name().equals(name)) {
                it.remove();
                return;
            }
        }
    }

    /**
     * <p>Removes all fields with the given name from the document.
     * If there is no field with the specified name, the document remains unchanged.</p>
     * <p> Note that the removeField(s) methods like the add method only make sense
     * prior to adding a document to an index. These methods cannot be used to change the content of
     * an existing index! In order to achieve this, a document has to be deleted from an index and a
     * new changed version of that document has to be added.</p>
     */
    public final void removeFields(String name) {
        Iterator<IndexableField> it = fields.iterator();
        while (it.hasNext()) {
            IndexableField field = it.next();
            if (field.name().equals(name)) {
                it.remove();
            }
        }
    }

    /**
     * Returns an array of byte arrays for of the fields that have the name specified as the method
     * parameter.  This method returns an empty array when there are no matching fields.  It never
     * returns null.
     *
     * @param name the name of the field
     * @return a <code>BytesRef[]</code> of binary field values
     */
    public final BytesRef[] getBinaryValues(String name) {
        final List<BytesRef> result = new ArrayList<>();
        for (IndexableField field : fields) {
            if (field.name().equals(name)) {
                final BytesRef bytes = field.binaryValue();
                if (bytes != null) {
                    result.add(bytes);
                }
            }
        }

        return result.toArray(new BytesRef[result.size()]);
    }

    /**
     * Returns an array of bytes for the first (or only) field that has the name specified as the
     * method parameter. This method will return <code>null</code> if no binary fields with the
     * specified name are available. There may be non-binary fields with the same name.
     *
     * @param name the name of the field.
     * @return a <code>BytesRef</code> containing the binary field value or <code>null</code>
     */
    public final BytesRef getBinaryValue(String name) {
        for (IndexableField field : fields) {
            if (field.name().equals(name)) {
                final BytesRef bytes = field.binaryValue();
                if (bytes != null) {
                    return bytes;
                }
            }
        }
        return null;
    }

    /**
     * Returns a field with the given name if any exist in this document, or null.  If multiple
     * fields exists with this name, this method returns the first value added.
     */
    public final IndexableField getField(String name) {
        for (IndexableField field : fields) {
            if (field.name().equals(name)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Returns an array of {@link IndexableField}s with the given name. This method returns an empty
     * array when there are no matching fields.  It never returns null.
     *
     * @param name the name of the field
     * @return a <code>Field[]</code> array
     */
    public IndexableField[] getFields(String name) {
        List<IndexableField> result = new ArrayList<>();
        for (IndexableField field : fields) {
            if (field.name().equals(name)) {
                result.add(field);
            }
        }

        return result.toArray(new IndexableField[result.size()]);
    }

    /**
     * Returns a List of all the fields in a document.
     *
     * @return an immutable <code>List&lt;Field&gt;</code>
     */
    public final List<IndexableField> getFields() {
        return Collections.unmodifiableList(fields);
    }

    /**
     * Returns an array of values of the field specified as the method parameter. This method
     * returns an empty array when there are no matching fields.  It never returns null.
     *
     * @param name the name of the field
     * @return a <code>String[]</code> of field values
     */
    public final String[] getValues(String name) {
        List<String> result = new ArrayList<>();
        for (IndexableField field : fields) {
            if (field.name().equals(name) && field.stringValue() != null) {
                result.add(field.stringValue());
            }
        }

        if (result.size() == 0) {
            return NO_STRINGS;
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * Returns the string value of the field with the given name if any exist in this document, or
     * null.  If multiple fields exist with this name, this method returns the first value added. If
     * only binary fields with this name exist, returns null.
     */
    public final String get(String name) {
        for (IndexableField field : fields) {
            if (field.name().equals(name) && field.stringValue() != null) {
                return field.stringValue();
            }
        }
        return null;
    }

    /**
     * Prints the fields of a document for human consumption.
     */
    @Override
    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Document<");
        for (int i = 0; i < fields.size(); i++) {
            IndexableField field = fields.get(i);
            buffer.append(field.toString());
            if (i != fields.size() - 1) {
                buffer.append(" ");
            }
        }
        buffer.append(">");
        return buffer.toString();
    }

    /**
     * Removes all the fields from document.
     */
    public void clear() {
        fields.clear();
    }
}