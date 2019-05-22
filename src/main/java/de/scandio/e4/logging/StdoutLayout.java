package de.scandio.e4.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;

public class StdoutLayout extends LayoutBase<ILoggingEvent> {
	@Override
	public String doLayout(ILoggingEvent iLoggingEvent) {
		return null;
	}
}
