package yongfa365.jmeter.assertions;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.compare.ComparableUtils;
import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.oro.text.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;


public class JSONPathAssertionPlus extends AbstractTestElement implements Serializable, Assertion, ThreadListener {
    private static final Logger log = LoggerFactory.getLogger(JSONPathAssertionPlus.class);
    private static final long serialVersionUID = 123123123123456L;
    public static final String JSONPATH = "JSON_PATH";
    public static final String EXPECTEDVALUE = "EXPECTED_VALUE";

    public static final String IS_DEFAULT = "IS_ASC";
    public static final String IS_DESC = "IS_DESC";

    public static final String IS_CUSTOM = "IS_USE_SIZE";
    public static final String ROOT_PATH = "MIN_COUNT";
    public static final String MAX_COUNT = "MAX_COUNT";

    public static final String IS_USE_MATCH = "IS_USE_MATCH";
    public static final String ISREGEX = "ISREGEX";
    public static final String IS_ALL_MATCH = "IS_ALL_MATCH";
    public static final String IS_ANY_MATCH = "IS_ANY_MATCH";

    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMATTER =
            ThreadLocal.withInitial(JSONPathAssertionPlus::createDecimalFormat);

    private static DecimalFormat createDecimalFormat() {
        DecimalFormat decimalFormatter = new DecimalFormat("#.#");
        decimalFormatter.setMaximumFractionDigits(340); // java.text.DecimalFormat.DOUBLE_FRACTION_DIGITS == 340
        decimalFormatter.setMinimumFractionDigits(1);
        return decimalFormatter;
    }

    public String getJsonPath() {
        return getPropertyAsString(JSONPATH);
    }

    public void setJsonPath(String jsonPath) {
        setProperty(JSONPATH, jsonPath);
    }

    public String getExpectedValue() {
        return getPropertyAsString(EXPECTEDVALUE);
    }

    public void setExpectedValue(String expectedValue) {
        setProperty(EXPECTEDVALUE, expectedValue);
    }


    public void setIsRegex(boolean flag) {
        setProperty(ISREGEX, flag);
    }

    public boolean isUseRegex() {
        return getPropertyAsBoolean(ISREGEX, true);
    }

    public boolean isAllMatch() {
        return isUseMatch() && getPropertyAsBoolean(IS_ALL_MATCH, false);
    }

    public boolean isAnyMatch() {
        return isUseMatch() && getPropertyAsBoolean(IS_ANY_MATCH, false);
    }

    public boolean isUseMatch() {
        return getPropertyAsBoolean(IS_USE_MATCH, false);
    }

    public boolean isAsc() {
        return getPropertyAsBoolean(IS_DEFAULT, false);
    }

    public boolean isDesc() {
        return getPropertyAsBoolean(IS_DESC, false);
    }

    public boolean isUseSize() {
        return getPropertyAsBoolean(IS_CUSTOM, false);
    }

    public int getMinCount() {
        return getPropertyAsInt(ROOT_PATH, Integer.MIN_VALUE);
    }

    public int getMaxCount() {
        return getPropertyAsInt(MAX_COUNT, Integer.MAX_VALUE);
    }

    private void doAssert(String jsonString) {
        Object value = JsonPath.read(jsonString, getJsonPath());

        if (isAsc()) {
            assertIsAsc(value);
        } else if (isDesc()) {
            assertIsDesc(value);
        } else if (isUseSize()) {
            assertJsonPathResultSize((JSONArray) value);
        } else {
            if (value instanceof JSONArray) {
                arrayMatched((JSONArray) value);
            } else {
                if (!isEquals(value)) {
                    String msg = String.format("\n【expected】\n%s\n\n【actual】:\n%s", getExpectedValue(), objectToString(value));
                    throw new RuntimeException(msg);
                }
            }
        }

    }

    /**
     * 断言size
     *
     * @param value
     */
    public void assertJsonPathResultSize(JSONArray value) {
        int begin = getMinCount();
        int end = getMaxCount();
        int actual = value.size();
        if (!ComparableUtils.between(begin, end).test(actual)) {
            String actualStr = value.stream().map(p -> objectToString(p))
                    .collect(Collectors.joining(",", "[", "]"));
            String msg = String.format("\n【JsonPath Result】\n%s\n【expected】\n%s <= count <= %s\n【actual】\n%s", actualStr, begin, end, actual);
            throw new RuntimeException(msg);
        }
    }

    private void assertIsAsc(Object input) {
        JSONArray value = toJsonArray(input);

        if (value.isEmpty() && "[]".equals(getExpectedValue())) {
            return;
        }

        String actual = value.stream().map(p -> objectToString(p))
                .collect(Collectors.joining(",", "[", "]"));
        String expected = value.stream().map(p -> objectToString(p))
                .sorted()
                .collect(Collectors.joining(",", "[", "]"));
        if (!actual.equals(expected)) {
            String msg = String.format("\n【expected】is asc\n%s\n\n【actual】:\n%s", expected, actual);
            throw new RuntimeException(msg);
        }
    }

    private void assertIsDesc(Object input) {
        JSONArray value = toJsonArray(input);

        if (value.isEmpty() && "[]".equals(getExpectedValue())) {
            return;
        }

        String actual = value.stream().map(p -> objectToString(p))
                .collect(Collectors.joining(",", "[", "]"));

        String expected = value.stream().map(p -> objectToString(p))
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.joining(",", "[", "]"));
        if (!actual.equals(expected)) {
            String msg = String.format("\n【expected】is desc \n%s\n\n【actual】:\n%s", expected, actual);
            throw new RuntimeException(msg);
        }

    }


    private void arrayMatched(JSONArray value) {
        //没返回数据就当正常
        if (value.isEmpty() && "[]".equals(getExpectedValue())) {
            return;
        }

        if (isAllMatch()) {
            boolean isOK = value.stream().allMatch(this::isEquals);
            if (!isOK) {
                String msg = String.format("\n【expected】all match\n%s\n\n【actual】:\n%s", getExpectedValue(), value.toJSONString());
                throw new RuntimeException(msg);
            }
        } else if (isAnyMatch()) {
            boolean isOK = value.stream().anyMatch(this::isEquals);
            if (!isOK) {
                String msg = String.format("\n【expected】any match\n%s\n\n【actual】:\n%s", getExpectedValue(), value.toJSONString());
                throw new RuntimeException(msg);
            }
        }
    }

    private boolean isEquals(Object subj) {
        String str = objectToString(subj);
        if (isUseRegex()) {
            Pattern pattern = JMeterUtils.getPatternCache().getPattern(getExpectedValue());
            return JMeterUtils.getMatcher().matches(str, pattern);
        } else {
            return str.equals(getExpectedValue());
        }
    }

    private JSONArray toJsonArray(Object input) {
        if (input instanceof JSONArray) {
            return (JSONArray) input;
        } else {
            throw new RuntimeException("value is not array");
        }
    }

    @Override
    public AssertionResult getResult(SampleResult samplerResult) {
        AssertionResult result = new AssertionResult(getName());
        String responseData = samplerResult.getResponseDataAsString();
        if (responseData.isEmpty()) {
            return result.setResultForNull();
        }

        result.setFailure(false);
        result.setFailureMessage("");

        try {
            doAssert(responseData);
        } catch (Exception e) {
            log.debug("Assertion failed", e);
            result.setFailure(true);
            result.setFailureMessage(e.getMessage());
        }

        return result;
    }

    public static String objectToString(Object subj) {
        String str;
        if (subj == null) {
            str = "null";
        } else if (subj instanceof Map) {
            //noinspection unchecked
            str = new JSONObject((Map<String, ?>) subj).toJSONString();
        } else if (subj instanceof Double || subj instanceof Float) {
            str = DECIMAL_FORMATTER.get().format(subj);
        } else {
            str = subj.toString();
        }
        return str;
    }

    @Override
    public void threadStarted() {
        // nothing to do on thread start
    }

    @Override
    public void threadFinished() {
        DECIMAL_FORMATTER.remove();
    }
}
